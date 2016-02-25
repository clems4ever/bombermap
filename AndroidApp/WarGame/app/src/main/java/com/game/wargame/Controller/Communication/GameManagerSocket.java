package com.game.wargame.Controller.Communication;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by clement on 25/02/16.
 */
public class GameManagerSocket {

    private IEventSocket mSocket;

    public GameManagerSocket(IEventSocket socket) {
        mSocket = socket;
    }

    public void connect() {
        mSocket.connect();
    }

    public void disconnect() {
        mSocket.disconnect();
    }

    public void createGame(final OnGameCreatedListener onGameCreatedListener) {
        mSocket.call("create_game", null, new IEventSocket.OnRemoteEventReceivedListener() {
            @Override
            public void onRemoteEventReceived(JSONObject message) {
                String gameId = null;
                try {
                    gameId = message.getString("game_id");
                    if(onGameCreatedListener != null) {
                        onGameCreatedListener.onGameCreated(gameId);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public interface OnGameCreatedListener {
        public void onGameCreated(String gameId);
    }

}
