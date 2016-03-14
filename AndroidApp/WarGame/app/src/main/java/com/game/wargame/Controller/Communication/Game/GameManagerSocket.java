package com.game.wargame.Controller.Communication.Game;

import com.game.wargame.Controller.Communication.ISocket;
import com.game.wargame.Controller.Communication.ISocketFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class GameManagerSocket {

    private ISocket mSocket;

    public GameManagerSocket(ISocket socket) {
        mSocket = socket;
    }

    public void createGame(final OnGameCreatedListener onGameCreatedListener) {
        JSONObject message = new JSONObject();
        try {
            message.put("action", "newgame");
            mSocket.call("global_queue", message, new ISocket.OnRemoteEventReceivedListener() {
                @Override
                public void onRemoteEventReceived(JSONObject message) throws JSONException {
                    String gameId = message.getString("game_id");
                    if (onGameCreatedListener != null) {
                        onGameCreatedListener.onGameCreated(gameId);
                    }
                }
            });
        } catch (JSONException e) {
        }

    }

    public String joinGame(final String gameId, final OnGameJoinedListener onGameJoinedListener) {
        JSONObject message = new JSONObject();
        String playerId = null;
        try {
            message.put("action", "join");
            message.put("game_id", gameId);

            mSocket.call("global_queue", message, new ISocket.OnRemoteEventReceivedListener() {
                @Override
                public void onRemoteEventReceived(JSONObject message) {
                    String playerId = null;
                    try {
                        playerId = message.getString("player_id");
                        //TODO: parse other players data here
                        onGameJoinedListener.onGameJoined(playerId);
                        //sendPlayerJoinMessage(playerId);
                    } catch (JSONException e) {
                    }
                }
            });
        } catch (JSONException e) {
        }

        return playerId;
    }

    public void leaveGame(final String gameId, final String playerId) {
        JSONObject message = new JSONObject();
        try {
            message.put("action", "leave");
            message.put("player_id", playerId);
            message.put("game_id", gameId);

            mSocket.call("global_queue", message, new ISocket.OnRemoteEventReceivedListener() {
                @Override
                public void onRemoteEventReceived(JSONObject message) {
                }
            });
        } catch (JSONException e) {
        }
    }

    private void sendPlayerJoinMessage(final String playerId) {
        JSONObject message = new JSONObject();
        try {
            message.put("player_id", playerId);
            mSocket.emit("player_join", message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public interface OnGameCreatedListener {
        public void onGameCreated(String gameId);
    }

    public interface OnGameJoinedListener {
        public void onGameJoined(String playerId);
    }

}
