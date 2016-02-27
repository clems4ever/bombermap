package com.game.wargame.Controller.Communication;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by clement on 25/02/16.
 */
public class GameManagerSocket {

    private IEventSocket mSocket;
    private IConnectionManager mConnectionManager;

    public GameManagerSocket(IEventSocket socket, IConnectionManager connectionManager) {
        mSocket = socket;
        mConnectionManager = connectionManager;
    }

    public void createGame(final OnGameCreatedListener onGameCreatedListener) {
        mSocket.call("create_game", null, new IEventSocket.OnRemoteEventReceivedListener() {
            @Override
            public void onRemoteEventReceived(JSONObject message) {
                String gameId = null;
                try {
                    gameId = message.getString("game_id");
                    if (onGameCreatedListener != null) {
                        onGameCreatedListener.onGameCreated(gameId);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public String joinGame(final String gameId, final OnGameJoinedListener onGameJoinedListener) {
        JSONObject message = new JSONObject();
        String playerId = null;
        try {
            message.put("game_id", gameId);

            mSocket.call("join_game", message, new IEventSocket.OnRemoteEventReceivedListener() {
                @Override
                public void onRemoteEventReceived(JSONObject message) {
                    String playerId = null;
                    try {
                        playerId = message.getString("player_id");
                        GameSocket gameSocket = mConnectionManager.buildGameSocket(gameId);
                        LocalPlayerSocket playerSocket = mConnectionManager.buildLocalPlayerSocket(gameId, playerId);

                        onGameJoinedListener.onGameJoined(gameSocket, playerSocket);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return playerId;
    }

    public interface OnGameCreatedListener {
        public void onGameCreated(String gameId);
    }

    public interface OnGameJoinedListener {
        public void onGameJoined(GameSocket gameSocket, LocalPlayerSocket localPlayerSocket);
    }

}
