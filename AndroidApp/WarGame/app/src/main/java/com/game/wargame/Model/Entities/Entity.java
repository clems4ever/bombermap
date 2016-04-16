package com.game.wargame.Model.Entities;

import com.game.wargame.Controller.Engine.DisplayTransaction;
import com.game.wargame.Views.Animations.Animation;
import com.google.android.gms.maps.model.LatLng;

import java.util.UUID;

/**
 * Created by sergei on 14/03/16.
 */
public abstract class Entity implements Updatable {

    protected Animation mAnimation;
    protected LatLng mPosition;
    protected double mDirection;
    protected String mUUID;
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

    @Override
    public void update(long ticks, int increment, EntitiesContainerUpdater entitiesContainerUpdater, DisplayTransaction displayTransaction) {
        if (mAnimation != null) {
            mAnimation.addTime(increment);
        }
    }

    public double getRadius() {return mRadius;}

    public String getOwner() {return mOwner;}
}

