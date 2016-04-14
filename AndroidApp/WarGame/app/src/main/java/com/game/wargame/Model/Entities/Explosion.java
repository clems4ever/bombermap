package com.game.wargame.Model.Entities;

import com.game.wargame.Model.Entities.Players.LocalPlayerModel;
import com.game.wargame.Model.Entities.Players.PlayerException;
import com.game.wargame.Model.Entities.Players.PlayerModel;
import com.game.wargame.Views.Animations.AnimationFactory;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by sergei on 14/03/16.
 */
public class Explosion extends Entity {

    private static final int STANDARD_DURATION = 800;
    private static final double EXPLOSION_RADIUS = 100;

    private double mTimeStart;
    private double mTimeEnd;

    public Explosion(String owner, double timestart, LatLng position, double direction) {
        mOwner = owner;
        mTimeStart = timestart;
        mTimeEnd = timestart + STANDARD_DURATION;
        mPosition = position;
        mDirection = direction;
        mRadius = EXPLOSION_RADIUS;
        mAnimation = AnimationFactory.buildExplosionAnimation();
    }

    public void update(long ticks, int increment) {
        super.update(ticks, increment);
        long time = ticks*increment;
        if (time >= mTimeEnd)
        {
            setToRemove(true);
        }
    }
}
