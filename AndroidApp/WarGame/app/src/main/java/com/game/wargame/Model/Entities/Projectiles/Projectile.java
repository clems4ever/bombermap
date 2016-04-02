package com.game.wargame.Model.Entities.Projectiles;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.util.TreeMap;
import java.util.UUID;

/**
 * Created by sergei on 01/03/16.
 */
public class Projectile {

    protected static final int DEFAULT_SPEED = 200;
    protected static final float DEFAULT_DELTA_T = 200;

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

        mDirection = (float) SphericalUtil.computeHeading(mPosition, mTarget) + 90;
    }

    public Projectile(LatLng start, LatLng end, double timestamp)
    {
        mPosition = start;
        mTarget = end;
        mTimeStart = timestamp;
        initTrajectory();
        mUUID = UUID.randomUUID().toString();
    }

    public boolean equals(Projectile other)
    {
        return this.getUUID().equals(other.getUUID());
    }

    public LatLng getPosition() {
        return mPosition;
    }

    public void setPosition(LatLng position) {
        mPosition = position;
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

    public double getDirection() {
        return mDirection;
    }

    public void setDirection(double direction) {
        mDirection = direction;
    }

    public double setTimestart() {
        return mTimeStart;
    }

    public void setTimestart(double timestamp) {
        mTimeStart = timestamp;
    }

    public String getUUID()
    {
        return mUUID;
    }

    public boolean isToDestroy() {
        return mToDestroy;
    }
    public void setToDestroy(boolean toDestroy) {
        mToDestroy = toDestroy;
    }

    protected final String mUUID;
    protected double mTimeStart;
    protected LatLng mPosition;
    protected LatLng mTarget;
    protected double mDirection;
    protected TreeMap<Double, LatLng> mTrajectory;
    private boolean mToDestroy;
}
