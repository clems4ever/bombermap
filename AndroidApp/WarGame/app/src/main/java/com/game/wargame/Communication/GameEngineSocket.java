package com.game.wargame.Communication;

import com.game.wargame.Communication.SocketIO.RemoteCommunicationSocket;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

/**
 * Created by clement on 11/02/16.
 */
public class GameEngineSocket {

    private IRemoteCommunicationSocket mSocket;
    private PlayerSocket mLocalPlayerSocket;

    public GameEngineSocket() {
        init(new RemoteCommunicationSocket());
    }

    public GameEngineSocket(IRemoteCommunicationSocket remoteCommunicationSocket) {
        init(remoteCommunicationSocket);
    }

    private void init(IRemoteCommunicationSocket remoteCommunicationSocket) {
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
        mSocket.on("player_join", new RemoteCommunicationSocket.OnRemoteEventReceivedListener() {
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
