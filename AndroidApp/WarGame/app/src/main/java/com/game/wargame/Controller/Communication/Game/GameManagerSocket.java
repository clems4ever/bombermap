package com.game.wargame.Controller.Communication.Game;

import com.game.wargame.Controller.Communication.IConnectionManager;
import com.game.wargame.Controller.Communication.IEventSocket;
import com.game.wargame.Controller.Communication.Socket;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by clement on 25/02/16.
 */
public class GameManagerSocket {

    private Socket mSocket;

    public GameManagerSocket(Socket socket) {
        mSocket = socket;
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
                        GameSocket gameSocket = mSocket.getConnectionManager().buildGameSocket(gameId);
                        LocalPlayerSocket playerSocket = mSocket.getConnectionManager().buildLocalPlayerSocket(gameId, playerId);

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
