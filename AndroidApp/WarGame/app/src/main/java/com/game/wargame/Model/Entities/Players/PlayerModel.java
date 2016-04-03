package com.game.wargame.Model.Entities.Players;

import com.game.wargame.Model.Entities.OnPlayerDiedListener;

/**
 * Created by clement on 19/02/16.
 */
public abstract class PlayerModel extends Player {

    protected OnPlayerPositionChangedListener mOnPlayerPositionChangedListener;
    protected OnPlayerWeaponTriggeredListener mOnPlayerWeaponTriggeredListener;
    protected OnPlayerDiedListener mOnPlayerDiedListener;

    public PlayerModel(String playerId, String playerName) {
        super(playerId, playerName);
    }

    public void setOnPlayerWeaponTriggeredListener(OnPlayerWeaponTriggeredListener onPlayerWeaponTriggeredListener) {
        mOnPlayerWeaponTriggeredListener = onPlayerWeaponTriggeredListener;
    }

    public void setOnPlayerPositionChangedListener(OnPlayerPositionChangedListener onPlayerPositionChangedListener) {
        mOnPlayerPositionChangedListener = onPlayerPositionChangedListener;
    }

    public void setOnPlayerDiedListener(OnPlayerDiedListener onPlayerDiedListener)
    {
        mOnPlayerDiedListener = onPlayerDiedListener;
    }
}
