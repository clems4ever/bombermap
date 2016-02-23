package com.game.wargame.Controller.Communication;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.UUID;

public class GameEngineSocket {

    private IEventSocket mSocket;
    private PlayerSocket mLocalPlayerSocket;

    public GameEngineSocket(IEventSocket remoteCommunicationSocket) {
        init(remoteCommunicationSocket);
    }

    private void init(IEventSocket remoteCommunicationSocket) {
        mSocket = remoteCommunicationSocket;

        UUID playerUUID = UUID.randomUUID();
        String playerId = playerUUID.toString();
        mLocalPlayerSocket = new PlayerSocket(playerId, remoteCommunicationSocket);
    }

    public void connect() {
        mSocket.connect();
    }

    public boolean isConnected() {
        return mSocket.isConnected();
    }

    public void disconnect() {
        mSocket.disconnect();
    }

    public void setOnDisconnectedListener(IEventSocket.OnDisconnectedListener onDisconnectedListener) {
        mSocket.setOnDisconnected(onDisconnectedListener);
    }

    public String joinGame(String username, final OnJoinedListener listener) {
        JSONObject message = new JSONObject();
        String playerId = null;
        try {
            message.put("username", username);

            mSocket.call("join", message, new IEventSocket.OnRemoteEventReceivedListener() {
                @Override
                public void onRemoteEventReceived(JSONObject message) {
                    if(listener != null) {
                        String playerId = null;
                        try {
                            playerId = message.getString("player_id");
                            listener.onJoined(playerId);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return playerId;
    }

    public void setOnPlayerJoinedListener(final OnPlayerJoinedListener onPlayerJoinedListener) {
        mSocket.on("player_join", new IEventSocket.OnRemoteEventReceivedListener() {
            @Override
            public void onRemoteEventReceived(JSONObject data) {
                try {
                    String playerId = data.getString("player_id");
                    PlayerSocket playerSocket = new PlayerSocket(playerId, mSocket);
                    onPlayerJoinedListener.onPlayerJoined(playerSocket);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public interface OnPlayerJoinedListener {
        public void onPlayerJoined(PlayerSocket playerSocket);
    }

    public interface OnJoinedListener {
        public void onJoined(String playerId);
    }

    public PlayerSocket getLocalPlayerSocket() {
        return mLocalPlayerSocket;
    }
}
