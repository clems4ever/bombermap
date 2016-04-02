package com.game.wargame.Model.Entities;

/**
 * Created by sergei on 15/03/16.
 */
public interface OnPlayerDiedListener {
    public void onDied(String deadId, String killerId);
}
