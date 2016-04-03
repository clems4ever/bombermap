package com.game.wargame.Model.Entities.Players;

/**
 * Created by sergei on 15/03/16.
 */
public interface OnPlayerRespawnListener {
    public void onRespawn(String playerId, double time);
}
