package com.game.wargame.Controller.Communication.Game;

import android.util.Log;

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
                    if (player != null)
                        player.onMove(latitude, longitude);
                    else
                        Log.w("move", "remote player " + playerId + " is null");
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

                    if (player != null)
                        player.onFire(latitude, longitude, time);
                    else
                        Log.w("fire", "remote player " + playerId + " is null");
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

                if (player != null)
                    player.onDie(killerId, time);
                else
                    Log.w("died", "remote player " + playerId + " is null");
            }
        });

        mSocket.on("respawn", new ISocket.OnRemoteEventReceivedListener() {
            @Override
            public void onRemoteEventReceived(JSONObject message) throws JSONException {
                String playerId = message.getString("player_id");
                double time = message.getDouble("time");
                RemotePlayerSocket player = mPlayerByPlayerId.get(playerId);
                if (player != null)
                    player.onRespawn(time);
                else
                    Log.w("respawn", "remote player " + playerId + " is null");
            }
        });

        mSocket.on("shield", new ISocket.OnRemoteEventReceivedListener() {
            @Override
            public void onRemoteEventReceived(JSONObject message) throws JSONException {
                String playerId = message.getString("player_id");
                double time = message.getDouble("time");
                boolean isShielded = message.getBoolean("shielded");
                RemotePlayerSocket player = mPlayerByPlayerId.get(playerId);
                if (player != null)
                    player.onShield(playerId, isShielded, time);
                else
                    Log.w("respawn", "remote player " + playerId + " is null");
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
