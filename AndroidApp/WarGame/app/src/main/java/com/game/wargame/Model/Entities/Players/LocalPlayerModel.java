package com.game.wargame.Model.Entities.Players;

import com.game.wargame.Controller.Communication.Game.LocalPlayerSocket;
import com.game.wargame.Controller.Sensors.OnLocationUpdatedListener;
import com.google.android.gms.maps.model.LatLng;


public class LocalPlayerModel extends PlayerModel implements OnLocationUpdatedListener {

    private LocalPlayerSocket mPlayerSocket;

    public LocalPlayerModel(String playerName, LocalPlayerSocket playerSocket) {
        super(playerSocket.getPlayerId(), playerName);

        mPlayerSocket = playerSocket;
    }

    public void fire(double latitude, double longitude, double time) {
        mPlayerSocket.fire(latitude, longitude, time);

        if(mOnPlayerWeaponTriggeredListener != null) {
            mOnPlayerWeaponTriggeredListener.onPlayerWeaponTriggeredListener(this, latitude, longitude, time);
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

    public void die(String killerId, double time) {
        mPlayerSocket.die(this.getPlayerId(), killerId, time);

        if (mOnPlayerDiedListener != null)
            mOnPlayerDiedListener.onDied(this.getPlayerId(), killerId, time);
    }

    public void leave() {
        mPlayerSocket.leave();
    }
}
