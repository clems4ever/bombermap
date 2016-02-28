package com.game.wargame.Controller.Communication.Game;

import com.game.wargame.Controller.Communication.ISocket;
import com.game.wargame.Controller.Communication.ISocketFactory;

import org.json.JSONException;
import org.json.JSONObject;

public class GameManagerSocket {

    private ISocket mSocket;
    private ISocketFactory mSocketFactory;

    public GameManagerSocket(ISocket socket, ISocketFactory socketFactory) {
        mSocket = socket;
        mSocketFactory = socketFactory;
    }

    public void createGame(final OnGameCreatedListener onGameCreatedListener) {
        mSocket.call("create_game", null, new ISocket.OnRemoteEventReceivedListener() {
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

            mSocket.call("join_game", message, new ISocket.OnRemoteEventReceivedListener() {
                @Override
                public void onRemoteEventReceived(JSONObject message) {
                    String playerId = null;
                    try {
                        playerId = message.getString("player_id");
                        GameSocket gameSocket = mSocketFactory.buildGameSocket(gameId);
                        LocalPlayerSocket playerSocket = mSocketFactory.buildLocalPlayerSocket(gameId, playerId);

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

    public void leaveGame(final String playerId) {
        JSONObject message = new JSONObject();
        try {
            message.put("player_id", playerId);

            mSocket.call("leave_game", message, new ISocket.OnRemoteEventReceivedListener() {
                @Override
                public void onRemoteEventReceived(JSONObject message) {
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public interface OnGameCreatedListener {
        public void onGameCreated(String gameId);
    }

    public interface OnGameJoinedListener {
        public void onGameJoined(GameSocket gameSocket, LocalPlayerSocket localPlayerSocket);
    }

}
