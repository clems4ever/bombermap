package com.game.wargame.Controller.Communication.Game;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by clement on 19/02/16.
 */
public abstract class PlayerSocket {

    protected String mPlayerId;

    /**
     *
     * @param playerId
     */
    public PlayerSocket(String playerId) {
        mPlayerId = playerId;
    }

    public String getPlayerId() {
        return mPlayerId;
    }
}
