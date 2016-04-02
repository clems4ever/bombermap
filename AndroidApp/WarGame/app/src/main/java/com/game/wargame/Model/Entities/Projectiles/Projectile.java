package com.game.wargame.Model.Entities.Projectiles;

import android.location.Location;

import com.game.wargame.Controller.Communication.Game.RemotePlayerSocket;
import com.game.wargame.Controller.GameLogic.OnCollisionListener;
import com.game.wargame.Controller.GameLogic.OnExplosionListener;
import com.game.wargame.Model.Entities.Entity;
import com.game.wargame.Model.Entities.Players.PlayerModel;
import com.game.wargame.Views.Animations.AnimationFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.util.TreeMap;
import java.util.UUID;

/**
 * Created by sergei on 01/03/16.
 */
public class Projectile extends Entity {

    protected static final int DEFAULT_SPEED = 200;
    protected static final float DEFAULT_DELTA_T = 200;
    private static final double PROJECTILE_RADIUS = 100;

    private OnExplosionListener mOnExplosion;
    private OnCollisionListener mOnCollision;

    private void initTrajectory() {
        double deltaLatitude = mTarget.latitude - mPosition.latitude;
        double deltaLongitude = mTarget.longitude - mPosition.longitude;

        float[] results = new float[1];
        Location.distanceBetween(mPosition.latitude, mPosition.longitude, mTarget.latitude, mTarget.longitude, results);

        double distance = results[0]; // in meters

        double totalTime = distance / DEFAULT_SPEED * 1000.0f;
        int occurrences = (int) (totalTime / DEFAULT_DELTA_T);

        double dLatitude = deltaLatitude / occurrences;
        double dLongitude = deltaLongitude / occurrences;

        mTrajectory = new TreeMap<Double, LatLng>();
        for(int i=0; i<occurrences; ++i) {
            LatLng position = new LatLng(mPosition.latitude + i * dLatitude, mPosition.longitude + i * dLongitude);
            double positionTime = mTimeStart + DEFAULT_DELTA_T*i;
            mTrajectory.put(positionTime, position);
        }

        mDirection = (float) SphericalUtil.computeHeading(mPosition, mTarget);
    }

    public Projectile(String owner, LatLng start, LatLng end, double timestamp) {
        mOwner = owner;
        mPosition = start;
        mTarget = end;
        mTimeStart = timestamp;
        mRadius = PROJECTILE_RADIUS;
        initTrajectory();
        mAnimation = AnimationFactory.buildProjectileAnimation();
    }

    public void setOnExplosionListener(OnExplosionListener onExplosionListener) {
        mOnExplosion = onExplosionListener;
    }


    public boolean equals(Projectile other) {
        return this.getUUID().equals(other.getUUID());
    }

    public LatLng getTarget() {
        return mTarget;
    }

    public void setTarget(LatLng target) {
        mTarget = target;
    }

    public TreeMap<Double, LatLng> getTrajectory() {
        return mTrajectory;
    }

    public void setTrajectory(TreeMap<Double, LatLng> trajectory) {
        mTrajectory = trajectory;
    }

    public double setTimestart() {
        return mTimeStart;
    }

    public void setTimestart(double timestamp) {
        mTimeStart = timestamp;
    }

    @Override
    public void update(long ticks, int increment)
    {
        double time = ticks*increment;
        Double newTimestamp = mTrajectory.ceilingKey(time);
        mAnimation.addTime(increment);

        if (newTimestamp != null) {
            LatLng newPosition = mTrajectory.get(newTimestamp);
            super.setPosition(newPosition);
            //TODO: evaluateContacts
        }
        else {
            mOnExplosion.onExplosion(this, (long)time);
        }
    }

    @Override
    public void onCollision(PlayerModel player, double time) {
        //mOnExplosion.onExplosion(this, (long) time);
    }

    protected double mTimeStart;
    protected LatLng mTarget;
    protected TreeMap<Double, LatLng> mTrajectory;
}
