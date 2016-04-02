package com.game.wargame.Model.Entities;

import com.game.wargame.Model.Entities.Players.Player;

/**
 * Created by sergei on 15/03/16.
 */
public interface OnPlayerDiedListener {
    public void onDied(Player dead, Player killer);
}
