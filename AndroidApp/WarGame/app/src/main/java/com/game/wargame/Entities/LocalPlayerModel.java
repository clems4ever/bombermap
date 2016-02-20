package com.game.wargame.Entities;

import com.game.wargame.Communication.PlayerSocket;
import com.game.wargame.OnLocationUpdatedListener;
import com.google.android.gms.maps.model.LatLng;


public class LocalPlayerModel extends PlayerModel {

    private PlayerSocket mPlayerSocket;

    public LocalPlayerModel(String playerName, PlayerSocket playerSocket) {
        super(playerName, playerSocket);

        mPlayerSocket = playerSocket;
    }

    public void fire(double latitude, double longitude, double speed) {
        mPlayerSocket.fire(latitude, longitude, speed);
    }

    @Override
    public void onLocationUpdated(double latitude, double longitude) {
        mPosition = new LatLng(latitude, longitude);

        mPlayerSocket.move(latitude, longitude);

        if(mOnPlayerPositionChangedListener != null) {
            mOnPlayerPositionChangedListener.onPlayerPositionChanged(this);
        }
    }


    @Override
    public void onFireEvent(double latitude, double longitude, double velocity) {
        if(mOnPlayerWeaponTriggeredListener != null) {
            mOnPlayerWeaponTriggeredListener.onPlayerWeaponTriggeredListener(this, latitude, longitude, velocity);
        }
    }
}
