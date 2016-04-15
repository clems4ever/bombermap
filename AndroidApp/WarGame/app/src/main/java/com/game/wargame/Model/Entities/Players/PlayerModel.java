package com.game.wargame.Model.Entities.Players;

import com.game.wargame.Views.Animations.Animation;
import com.game.wargame.Views.Animations.AnimationFactory;

/**
 * Created by clement on 19/02/16.
 */
public abstract class PlayerModel extends Player {

    protected OnPlayerWeaponTriggeredListener mOnPlayerWeaponTriggeredListener;
    protected OnPlayerDiedListener mOnPlayerDiedListener;
    protected OnPlayerRespawnListener mOnPlayerRespawnListener;
    protected OnPlayerShielded mOnPlayerShieldListener;

    public PlayerModel(String playerId, String playerName) {
        super(playerId, playerName);
    }

    public void setOnPlayerWeaponTriggeredListener(OnPlayerWeaponTriggeredListener onPlayerWeaponTriggeredListener) {
        mOnPlayerWeaponTriggeredListener = onPlayerWeaponTriggeredListener;
    }

    public void setOnPlayerDiedListener(OnPlayerDiedListener onPlayerDiedListener)
    {
        mOnPlayerDiedListener = onPlayerDiedListener;
    }

    public void setOnPlayerRespawnListener(OnPlayerRespawnListener onPlayerRespawnListener) {
        mOnPlayerRespawnListener = onPlayerRespawnListener;
    }

    public void setOnPlayerShieldListener(OnPlayerShielded onPlayerShielded) {
        mOnPlayerShieldListener = onPlayerShielded;
    }
}
