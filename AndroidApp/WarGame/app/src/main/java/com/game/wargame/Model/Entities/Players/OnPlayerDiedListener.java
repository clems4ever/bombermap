package com.game.wargame.Model.Entities.Players;

import com.game.wargame.Model.Entities.Players.Player;

/**
 * Created by sergei on 15/03/16.
 */
public interface OnPlayerDiedListener {
    public void onDied(String deadId, String killerId, double time);
}
