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
            public void onRemoteEventReceived(JSONObject message) {
                try {
                    String playerId = message.getString("player_id");
                    RemotePlayerSocket player = mPlayerByPlayerId.get(playerId);

                    if(player != null) {
                        double latitude = message.getDouble("lat");
                        double longitude = message.getDouble("long");
                        player.onMove(latitude, longitude);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        mSocket.on("fire", new ISocket.OnRemoteEventReceivedListener() {
            @Override
            public void onRemoteEventReceived(JSONObject message) {
                try {
                    String playerId = message.getString("player_id");
                    RemotePlayerSocket player = mPlayerByPlayerId.get(playerId);

                    if(player != null) {
                        double latitude = message.getDouble("lat");
                        double longitude = message.getDouble("long");
                        double speed = message.getDouble("speed");

                        player.onFire(latitude, longitude, speed);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
