package com.game.wargame.Controller.Communication;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by clement on 19/02/16.
 */
public abstract class PlayerSocket {

    protected IEventSocket mSocket;
    protected String mPlayerId;

    /**
     *
     * @param playerId
     * @param eventSocket
     */
    public PlayerSocket(String playerId, IEventSocket eventSocket) {
        mSocket = eventSocket;
        mPlayerId = playerId;
    }

    public String getPlayerId() {
        return mPlayerId;
    }
}
