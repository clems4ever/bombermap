package com.game.wargame.Entities;

import com.game.wargame.Communication.PlayerSocket;
import com.game.wargame.GameEngine.GameEngine;
import com.game.wargame.OnLocationUpdatedListener;

/**
 * Created by clement on 19/02/16.
 */
public abstract class PlayerModel extends Player {

    protected OnPlayerPositionChangedListener mOnPlayerPositionChangedListener;
    protected OnPlayerWeaponTriggeredListener mOnPlayerWeaponTriggeredListener;

    public PlayerModel(String playerId, String playerName) {
        super(playerId, playerName);
    }

    public void setOnPlayerWeaponTriggeredListener(OnPlayerWeaponTriggeredListener onPlayerWeaponTriggeredListener) {
        mOnPlayerWeaponTriggeredListener = onPlayerWeaponTriggeredListener;
    }

    public void setOnPlayerPositionChangedListener(OnPlayerPositionChangedListener onPlayerPositionChangedListener) {
        mOnPlayerPositionChangedListener = onPlayerPositionChangedListener;
    }
}
