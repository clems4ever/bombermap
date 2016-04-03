package com.game.wargame.Model.Entities.Players;

import com.game.wargame.Controller.Communication.Game.LocalPlayerSocket;
import com.google.android.gms.maps.model.LatLng;

/**
 * This local player can be locked
 */
public class LocalPlayerModel extends PlayerModel {

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

    public void move(double latitude, double longitude) {
        mPosition = new LatLng(latitude, longitude);
        mPlayerSocket.move(mPosition.latitude, mPosition.longitude);
    }

    public void moveShadow(double latitude, double longitude) {
        mShadowPosition = new LatLng(latitude, longitude);
    }

    public void die(String killerId, double time) {
        if (mRespawnCounter == 0) {
            mPlayerSocket.die(this.getPlayerId(), killerId, time);

            mRespawnCounter = TIME_TO_RESPAWN;

            if (mOnPlayerDiedListener != null) {
                mOnPlayerDiedListener.onDied(this.getPlayerId(), killerId, time);
            }
        }
    }

    public void respawn(double time) {
        mPlayerSocket.respawn(time);
    }

    public void leave() {
        mPlayerSocket.leave();
    }

    public void update(long ticks, int increment) {
        if (mRespawnCounter > increment)
            mRespawnCounter -= increment;
        else if (mRespawnCounter > 0) {
            respawn(ticks*increment);
            mRespawnCounter = 0;
        }
    }
}
