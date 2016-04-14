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
public class Entity implements Updatable {

    protected Animation mAnimation;
    protected LatLng mPosition;
    protected double mDirection;
    protected String mUUID;
    protected boolean mToRemove;
    protected double mRadius;
    protected String mOwner;
    protected boolean mIsDisplayed = true;

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

    @Override
    public void update(long ticks, int increment) {
        if (mAnimation != null)
            mAnimation.addTime(increment);
    }

    public double getRadius() {return mRadius;}

    public String getOwner() {return mOwner;}

    public void setDisplayed(boolean displayed) {
        mIsDisplayed = displayed;
    }

    public boolean isDisplayed() {
        return mIsDisplayed;
    }
}

