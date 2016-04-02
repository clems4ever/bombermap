package com.game.wargame.Model.Entities.Players;

import com.game.wargame.Controller.Communication.Game.RemotePlayerSocket;
import com.game.wargame.Model.Entities.Players.PlayerModel;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by clement on 19/02/16.
 */
public class RemotePlayerModel extends PlayerModel implements RemotePlayerSocket.OnMoveEventListener, RemotePlayerSocket.OnFireEventListener, RemotePlayerSocket.OnDieEventListener{

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

    @Override
    public void onDieEvent(String playerId, String killerId, double time)
    {
        if(mOnPlayerDiedListener != null)
            mOnPlayerDiedListener.onDied(playerId, killerId, time);
    }
}
