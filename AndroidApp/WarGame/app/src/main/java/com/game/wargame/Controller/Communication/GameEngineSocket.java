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

    public String joinGame(String username) {
        JSONObject message = new JSONObject();
        String playerId = null;
        try {
            message.put("username", username);
            JSONObject retValue = mSocket.call("join", message);

            if(retValue != null) {
                playerId = retValue.getString("player_id");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
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

    public PlayerSocket getLocalPlayerSocket() {
        return mLocalPlayerSocket;
    }
}
