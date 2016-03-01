package com.game.wargame.Model.Entities;

import com.game.wargame.Controller.Communication.Game.RemotePlayerSocket;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by sergei on 01/03/16.
 */
public class Projectile {
    public LatLng getPosition() {
        return mPosition;
    }

    public void setPosition(LatLng position) {
        mPosition = position;
    }

    public double getDirection() {
        return mDirection;
    }

    public void setDirection(double direction) {
        mDirection = direction;
    }

    public double getTimeOfDestroy() {
        return mTimeOfDestroy;
    }

    public void setTimeOfDestroy(double timestamp) {
        mTimeOfDestroy = timestamp;
    }

    private OnContactListener mOnContactListener;
    protected double mDirection;
    protected LatLng mPosition;
    protected double mTimeOfDestroy;
}
