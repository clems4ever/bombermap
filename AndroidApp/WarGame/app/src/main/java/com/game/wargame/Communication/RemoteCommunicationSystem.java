package com.game.wargame.Communication;

import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Ack;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

/**
 * Created by clement on 11/02/16.
 */
public class RemoteCommunicationSystem {

    private IRemoteCommunicationSocket mSocket;

    public RemoteCommunicationSystem() {
        mSocket = new RemoteCommunicationSocket();
    }

    public RemoteCommunicationSystem(IRemoteCommunicationSocket remoteCommunicationSocket) {
        mSocket = remoteCommunicationSocket;
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

    public void createGame(final OnGameCreatedListener onGameCreatedListener) {
        mSocket.emit("new_game", new Ack() {
            @Override
            public void call(Object... args) {
                String gameId = (String) args[0];

                Log.d("RemoteCommunication", "Received " + gameId);
                onGameCreatedListener.onGameCreated(gameId);
            }
        });
    }

    public void joinGame(String gameId, final String username, final OnPlayerJoinedListener onPlayerJoinedListener) {
        JSONObject data = new JSONObject();
        try {
            data.put("game_id", gameId);
            data.put("username", username);

            mSocket.emit("join_game", data, new Ack() {
                @Override
                public void call(Object... args) {
                    JSONObject reply = (JSONObject) args[0];
                    try {
                        String playerId = reply.getString("player_id");
                        String name = reply.getString("username");
                        onPlayerJoinedListener.onPlayerJoined(playerId, name);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void startGame() {
        mSocket.emit("start_game");
    }

    public void move(double latitude, double longitude, float rotation) {
        JSONObject moveJsonObject = new JSONObject();

        try {
            moveJsonObject.put("x", latitude);
            moveJsonObject.put("y", longitude);
            moveJsonObject.put("rotation", rotation);

            mSocket.emit("move", moveJsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setOnPositionUpdatedListener(final OnPositionUpdatedListener onPositionUpdatedListener) {
        mSocket.on("m", new RemoteCommunicationSocket.OnRemoteEventReceivedListener() {
            @Override
            public void onRemoteEventReceived(Object... args) {
                JSONObject data = (JSONObject) args[0];
                float latitude;
                float longitude;
                float rotation;

                try {
                    latitude = (float) data.getDouble("x");
                    longitude = (float) data.getDouble("y");
                    rotation = (float) data.getDouble("rotation");

                    onPositionUpdatedListener.onPositionUpdated(latitude, longitude, rotation);

                    Log.d("Position", String.valueOf(latitude) + "-" + String.valueOf(longitude) + "-" + String.valueOf(rotation));
                } catch (JSONException e) {
                    return;
                }
            }
        });
    }

    public void setOnPlayerJoinedListener(final OnPlayerJoinedListener onPlayerJoinedListener) {
        mSocket.on("players_joined", new RemoteCommunicationSocket.OnRemoteEventReceivedListener() {
            @Override
            public void onRemoteEventReceived(Object... args) {
                JSONObject data = (JSONObject) args[0];
                try {
                    String playerId = data.getString("player_id");
                    String name = data.getString("username");
                    onPlayerJoinedListener.onPlayerJoined(playerId, name);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void setOnPlayerLeftListener(final OnPlayerLeftListener onPlayerLeftListener) {
        mSocket.on("players_left", new RemoteCommunicationSocket.OnRemoteEventReceivedListener() {
            @Override
            public void onRemoteEventReceived(Object... args) {
                JSONObject data = (JSONObject) args[0];
                try {
                    String playerId = data.getString("player_id");
                    onPlayerLeftListener.onPlayerLeft(playerId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void setOnGameStartedListener(final OnGameStartedListener onGameStartedListener) {
        mSocket.on("game_started", new RemoteCommunicationSocket.OnRemoteEventReceivedListener() {
            @Override
            public void onRemoteEventReceived(Object... args) {
                onGameStartedListener.onGameStarted();
            }
        });
    }



    public interface OnPositionUpdatedListener {
        public void onPositionUpdated(float latitude, float longitude, float rotation);
    }

    public interface OnPlayerJoinedListener {
        public void onPlayerJoined(String playerId, String name);
    }

    public interface OnPlayerLeftListener {
        public void onPlayerLeft(String playerId);
    }

    public interface OnGameCreatedListener {
        public void onGameCreated(String gameId);
    }

    public interface OnGameStartedListener {
        public void onGameStarted();
    }

}
