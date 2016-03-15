package com.game.wargame.Model.Entities;

import com.game.wargame.Views.Animations.AnimationFactory;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by sergei on 14/03/16.
 */
public class Explosion extends Entity {

    private static final int STANDARD_DURATION = 1000;
    private static final double EXPLOSION_RADIUS = 200;

    private double mTimeStart;
    private double mTimeEnd;

    public Explosion(double timestart, LatLng position, double direction) {
        mTimeStart = timestart;
        mTimeEnd = timestart + STANDARD_DURATION;
        mPosition = position;
        mDirection = direction;
        mRadius = EXPLOSION_RADIUS;
        mAnimation = AnimationFactory.buildExplosionAnimation();
    }

    public void update(long ticks, int increment) {
        long time = ticks*increment;
        if (time < mTimeEnd)
        {
            mAnimation.addTime(increment);
        }
        else
        {
            setToRemove(true);
        }
    }

    @Override
    public void onCollision(PlayerModel player, double time) {
        try {
            //a collision with an explosion kills the player
            player.setHealth(0);
        }
        catch (PlayerException e)
        {
            e.printStackTrace();
        }

    }
}
