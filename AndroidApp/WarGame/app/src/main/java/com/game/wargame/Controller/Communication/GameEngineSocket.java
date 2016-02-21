package com.game.wargame.Controller.Communication;

import org.json.JSONException;
import org.json.JSONObject;

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

    public void connect(String gameRoom) {
        mSocket.connect(gameRoom);
    }

    public boolean isConnected() {
        return mSocket.isConnected();
    }

    public void disconnect() {
        mSocket.disconnect();
    }

    public void joinGame(String playerId, String username) {
        JSONObject message = new JSONObject();
        try {
            message.put("player_id", playerId);
            message.put("username", username);
            mSocket.emit("player_join", message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

    public PlayerSocket getLocalPlayerSocket() {
        return mLocalPlayerSocket;
    }
}
