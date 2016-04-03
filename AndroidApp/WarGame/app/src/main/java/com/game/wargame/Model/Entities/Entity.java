package com.game.wargame.Model.Entities;

import com.game.wargame.Controller.Engine.GlobalTimer;
import com.game.wargame.Model.Entities.Players.LocalPlayerModel;
import com.game.wargame.Model.Entities.Players.PlayerModel;
import com.game.wargame.Views.Animations.Animation;
import com.google.android.gms.maps.model.LatLng;

import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by sergei on 14/03/16.
 */
public abstract class Entity {

    protected Animation mAnimation;
    protected LatLng mPosition;
    protected double mDirection;
    protected String mUUID;
    protected boolean mToRemove;
    private ReentrantLock mLock;
    protected double mRadius;
    protected String mOwner;

    public Entity() {
        mUUID = UUID.randomUUID().toString();
    }

    public Animation getAnimation() {
        return mAnimation;
    }

    public LatLng getPosition() {
        return mPosition;
    }

    public void setPosition(LatLng pos) {
        mPosition = pos;
    }

    public double getDirection() {
        return mDirection;
    }

    public String getUUID() {
        return mUUID;
    }

    public void setToRemove(boolean toRemove) {
        mToRemove = toRemove;
    }

    public boolean isToRemove() {
        return mToRemove;
    }

    public abstract void update(long ticks, int increment);

    public double getRadius() {return mRadius;}

    public String getOwner() {return mOwner;}

    public abstract void onCollision(LocalPlayerModel player, double time);
}

