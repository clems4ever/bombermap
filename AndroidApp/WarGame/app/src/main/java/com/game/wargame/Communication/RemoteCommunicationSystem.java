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

    private RemoteCommunicationSocket mSocket;

    public RemoteCommunicationSystem() {
        mSocket = new RemoteCommunicationSocket();
    }

    public void connect() {
        mSocket.connect();
    }

    public void disconnect() {
        mSocket.disconnect();
    }

    public void createGame(final OnGameCreatedListener onGameCreatedListener) {
        mSocket.emit("new_game", new Ack() {
            @Override
            public void call(Object... args) {
                String gameId = (String) args[0];

                Log.d("RemoteCommunicati", "Received " + gameId);
                onGameCreatedListener.onGameCreated(gameId);
            }
        });
    }

    public void joinGame(String gameId, String username, final OnGameJoinedListener onGameJoinedListener) {
        JSONObject data = new JSONObject();
        try {
            data.put("game_id", gameId);
            data.put("username", username);

            mSocket.emit("join_game", data, new Ack() {
                @Override
                public void call(Object... args) {
                    onGameJoinedListener.onGameJoined();
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
                    Integer playerId = data.getInt("player_id");
                    String name = data.getString("name");
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
                    int playerId = data.getInt("player_id");
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
        public void onPlayerJoined(int playerId, String name);
    }

    public interface OnGameJoinedListener {
        public void onGameJoined();
    }

    public interface OnPlayerLeftListener {
        public void onPlayerLeft(int playerId);
    }

    public interface OnGameCreatedListener {
        public void onGameCreated(String gameId);
    }

    public interface OnGameStartedListener {
        public void onGameStarted();
    }

}
