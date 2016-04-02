package com.game.wargame.Controller.Communication.Game;

import com.game.wargame.Controller.Communication.ISocket;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RemotePlayersSocket {

    private ISocket mSocket;

    private Map<String, RemotePlayerSocket> mPlayerByPlayerId;


    public RemotePlayersSocket(ISocket socket) {
        mSocket = socket;

        mPlayerByPlayerId = new HashMap<>();

        mSocket.on("move", new ISocket.OnRemoteEventReceivedListener() {
            @Override
            public void onRemoteEventReceived(JSONObject message) throws JSONException {
                String playerId = message.getString("player_id");
                RemotePlayerSocket player = mPlayerByPlayerId.get(playerId);

                if(player != null) {
                    double latitude = message.getDouble("lat");
                    double longitude = message.getDouble("long");
                    player.onMove(latitude, longitude);
                }
            }
        });

        mSocket.on("fire", new ISocket.OnRemoteEventReceivedListener() {
            @Override
            public void onRemoteEventReceived(JSONObject message) throws JSONException {
                String playerId = message.getString("player_id");
                RemotePlayerSocket player = mPlayerByPlayerId.get(playerId);

                if(player != null) {
                    double latitude = message.getDouble("lat");
                    double longitude = message.getDouble("long");
                    double time = message.getDouble("time");

                    player.onFire(latitude, longitude, time);
                }
            }
        });

        mSocket.on("died", new ISocket.OnRemoteEventReceivedListener() {
            @Override
            public void onRemoteEventReceived(JSONObject message) throws JSONException {
                String playerId = message.getString("player_id");
                String killerId = message.getString("killer_id");
                double time = message.getDouble("time");
                RemotePlayerSocket player = mPlayerByPlayerId.get(playerId);
                player.onDie(killerId, time);

            }
        });
    }

    public void addPlayer(RemotePlayerSocket socket) {
        mPlayerByPlayerId.put(socket.getPlayerId(), socket);
    }

    public void removePlayer(RemotePlayerSocket socket) {
        mPlayerByPlayerId.remove(socket.getPlayerId());
    }
}
