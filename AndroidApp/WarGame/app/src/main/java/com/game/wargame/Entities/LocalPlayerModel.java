package com.game.wargame.Entities;

import com.game.wargame.Communication.PlayerSocket;
import com.game.wargame.GameEngine.GameView;
import com.game.wargame.OnLocationUpdatedListener;
import com.game.wargame.Sensors.LocationRetriever;
import com.google.android.gms.maps.model.LatLng;


public class LocalPlayerModel extends PlayerModel implements OnLocationUpdatedListener {

    private PlayerSocket mPlayerSocket;

    public LocalPlayerModel(String playerName, PlayerSocket playerSocket) {
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
}
