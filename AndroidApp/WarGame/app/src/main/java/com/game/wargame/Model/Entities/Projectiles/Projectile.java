package com.game.wargame.Model.Entities.Projectiles;

import android.location.Location;

import com.game.wargame.Controller.Engine.DisplayCommands.AddExplosionDisplayCommand;
import com.game.wargame.Controller.Engine.DisplayCommands.RemoveProjectileDisplayCommand;
import com.game.wargame.Controller.Engine.DisplayCommands.UpdateProjectileDisplayCommand;
import com.game.wargame.Controller.Engine.DisplayTransaction;
import com.game.wargame.Model.Entities.EntitiesContainerUpdater;
import com.game.wargame.Model.Entities.Entity;
import com.game.wargame.Model.Entities.Explosion;
import com.game.wargame.Views.Animations.AnimationFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.util.TreeMap;

/**
 * Created by sergei on 01/03/16.
 */
public class Projectile extends Entity {

    protected static final int DEFAULT_SPEED = 20;
    protected static final float DEFAULT_DELTA_T = 200;
    private static final double PROJECTILE_RADIUS = 10;

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

    public void setTrajectory(TreeMap<Double, LatLng> trajectory) {
        mTrajectory = trajectory;
    }

    @Override
    public void update(long ticks, int increment, EntitiesContainerUpdater entitiesContainerUpdater, DisplayTransaction displayTransaction) {
        super.update(ticks, increment, entitiesContainerUpdater, displayTransaction);
        double time = ticks * increment;
        Double newTimestamp = mTrajectory.ceilingKey(time);

        if (newTimestamp != null) {
            LatLng newPosition = mTrajectory.get(newTimestamp);
            super.setPosition(newPosition);
            //TODO: evaluateContacts
            displayTransaction.add(new UpdateProjectileDisplayCommand(this));
        } else {
            Explosion explosion = new Explosion(this.getOwner(), time, this.getPosition(), this.getDirection());
            entitiesContainerUpdater.addExplosion(explosion);
            entitiesContainerUpdater.removeProjectile(this);

            displayTransaction.add(new UpdateProjectileDisplayCommand(this));
            displayTransaction.add(new RemoveProjectileDisplayCommand(this));
            displayTransaction.add(new AddExplosionDisplayCommand(explosion));
        }
    }

    protected double mTimeStart;
    protected LatLng mTarget;
    protected TreeMap<Double, LatLng> mTrajectory;
}
