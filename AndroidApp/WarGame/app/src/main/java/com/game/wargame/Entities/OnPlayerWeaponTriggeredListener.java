package com.game.wargame.Entities;

/**
 * Created by clement on 20/02/16.
 */
public interface OnPlayerWeaponTriggeredListener {

    public void onPlayerWeaponTriggeredListener(PlayerModel player, double latitude, double longitude, double speed);
}
