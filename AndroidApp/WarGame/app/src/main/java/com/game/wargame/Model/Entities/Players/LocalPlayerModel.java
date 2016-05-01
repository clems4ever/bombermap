package com.game.wargame.Model.Entities.Players;

import com.game.wargame.Controller.Communication.Game.LocalPlayerSocket;
import com.game.wargame.Controller.Engine.DisplayTransaction;
import com.game.wargame.Model.Entities.EntitiesContainerUpdater;
import com.game.wargame.Model.Entities.Updatable;
import com.game.wargame.Views.Animations.AnimationFactory;
import com.google.android.gms.maps.model.LatLng;

/**
 * This local player can be locked
 */
public class LocalPlayerModel extends PlayerModel implements Updatable {

    private LocalPlayerSocket mPlayerSocket;

    private LatLng mShadowPosition;

    public LocalPlayerModel(String playerName, LocalPlayerSocket playerSocket) {
        super(playerSocket.getPlayerId(), playerName);

        mPlayerSocket = playerSocket;
        mShadowPosition = new LatLng(0, 0);
    }

    public LatLng getShadowPosition() {
        return mShadowPosition;
    }

    public void fire(double latitude, double longitude, double time) {
        mPlayerSocket.fire(latitude, longitude, time);

        if(mOnPlayerWeaponTriggeredListener != null) {
            mOnPlayerWeaponTriggeredListener.onPlayerWeaponTriggeredListener(this, latitude, longitude, time);
        }
    }

    public void move(double latitude, double longitude, double time) {
        mPosition = new LatLng(latitude, longitude);
        mPlayerSocket.move(mPosition.latitude, mPosition.longitude, time);
    }

    public void moveShadow(double latitude, double longitude) {
        mShadowPosition = new LatLng(latitude, longitude);
    }

    public void die(String killerId, double time) {
        if (mRespawnCounter == 0 && !isShielded()) {
            mPlayerSocket.die(this.getPlayerId(), killerId, time);

            mRespawnCounter = TIME_TO_RESPAWN;

            mAnimation = AnimationFactory.buildPlayerDeadAnimation(1);

            if (mOnPlayerDiedListener != null) {
                mOnPlayerDiedListener.onDied(this.getPlayerId(), killerId, time);
            }
        }
    }

    public void syncGameStart(double time) {
        mPlayerSocket.gameStart(time);
    }

    public void respawn(double time) {
        mAnimation = AnimationFactory.buildPlayerAliveAnimation(1);
        mPlayerSocket.respawn(time);
    }

    public void shield(double time) {
        super.shield();
        if (isShielded())
            mAnimation = AnimationFactory.buildPlayerShieldedAnimation(1);
        else
            mAnimation = AnimationFactory.buildPlayerAliveAnimation(1);
        mPlayerSocket.shield(time, !isShielded());
    }

    public void leave() {
        mPlayerSocket.leave();
    }

    @Override
    public void update(long ticks, int increment, EntitiesContainerUpdater entitiesContainerUpdater, DisplayTransaction displayTransaction) {
        super.update(ticks, increment, entitiesContainerUpdater, displayTransaction);
        if (mRespawnCounter > increment)
            mRespawnCounter -= increment;
        else if (mRespawnCounter > 0) {
            respawn(ticks*increment);
            mRespawnCounter = 0;
        }
    }
}
