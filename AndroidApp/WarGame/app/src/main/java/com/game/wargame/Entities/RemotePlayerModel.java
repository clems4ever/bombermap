package com.game.wargame.Entities;

import com.game.wargame.Communication.PlayerSocket;
import com.game.wargame.OnLocationUpdatedListener;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by clement on 19/02/16.
 */
public class RemotePlayerModel extends PlayerModel {

    public RemotePlayerModel(String playerName, PlayerSocket playerSocket) {
        super(playerName, playerSocket);
    }

    @Override
    public void onLocationUpdated(double latitude, double longitude) {
        mPosition = new LatLng(latitude, longitude);

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
