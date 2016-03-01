package com.game.wargame.Model.Entities;

import com.game.wargame.Controller.Communication.Game.LocalPlayerSocket;
import com.game.wargame.Controller.Sensors.OnLocationUpdatedListener;
import com.google.android.gms.maps.model.LatLng;


public class LocalPlayerModel extends PlayerModel implements OnLocationUpdatedListener {

    private LocalPlayerSocket mPlayerSocket;

    public LocalPlayerModel(String playerName, LocalPlayerSocket playerSocket) {
        super(playerSocket.getPlayerId(), playerName);

        mPlayerSocket = playerSocket;
    }

    public void fire(double latitude, double longitude, double speed) {
        mPlayerSocket.fire(latitude, longitude, speed);

        if(mOnPlayerWeaponTriggeredListener != null) {
            mOnPlayerWeaponTriggeredListener.onPlayerWeaponTriggeredListener(this, latitude, longitude, speed);
        }
    }

    @Override
    public void onLocationUpdated(double latitude, double longitude) {
        mPosition = new LatLng(latitude, longitude);
        mPlayerSocket.move(mPosition.latitude, mPosition.longitude);

        if(mOnPlayerPositionChangedListener != null) {
            mOnPlayerPositionChangedListener.onPlayerPositionChanged(this);
        }
    }

    public void leave() {
        mPlayerSocket.leave();
    }
}
