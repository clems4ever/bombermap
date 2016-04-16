package com.game.wargame.Model.Entities;

import com.game.wargame.Controller.Engine.DisplayCommands.RemoveExplosionDisplayCommand;
import com.game.wargame.Controller.Engine.DisplayCommands.UpdateExplosionDisplayCommand;
import com.game.wargame.Controller.Engine.DisplayTransaction;
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

    @Override
    public void update(long ticks, int increment, EntitiesContainer entitiesContainer, DisplayTransaction displayTransaction) {
        super.update(ticks, increment, entitiesContainer, displayTransaction);
        long time = ticks*increment;
        if (time >= mTimeEnd)
        {
            displayTransaction.add(new RemoveExplosionDisplayCommand(this));
            entitiesContainer.removeExplosion(this);
        }
        else {
            displayTransaction.add(new UpdateExplosionDisplayCommand(this));
        }
    }
}
