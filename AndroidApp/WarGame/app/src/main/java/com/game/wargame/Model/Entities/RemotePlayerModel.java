package com.game.wargame.Model.Entities;

import com.game.wargame.Controller.Communication.Game.RemotePlayerSocket;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by clement on 19/02/16.
 */
public class RemotePlayerModel extends PlayerModel implements RemotePlayerSocket.OnMoveEventListener, RemotePlayerSocket.OnFireEventListener{

    protected RemotePlayerSocket mPlayerSocket;

    public RemotePlayerModel(String playerName, RemotePlayerSocket playerSocket) {
        super(playerSocket.getPlayerId(), playerName);

        mPlayerSocket = playerSocket;

        mPlayerSocket.setOnFireEventListener(this);
        mPlayerSocket.setOnMoveEventListener(this);
    }

    @Override
    public void onFireEvent(double latitude, double longitude, double speed) {
        if(mOnPlayerWeaponTriggeredListener != null) {
            mOnPlayerWeaponTriggeredListener.onPlayerWeaponTriggeredListener(this, latitude, longitude, speed);
        }
    }

    @Override
    public void onMoveEvent(double latitude, double longitude) {
        mPosition = new LatLng(latitude, longitude);

        if(mOnPlayerPositionChangedListener != null) {
            mOnPlayerPositionChangedListener.onPlayerPositionChanged(this);
        }
    }
}
