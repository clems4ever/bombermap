package com.game.wargame.Controller.Communication.Game;

import com.game.wargame.Controller.Communication.IEventSocket;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RemotePlayerSocketRouter {

    private IEventSocket mSocket;

    private Map<String, RemotePlayerSocket> mPlayerByPlayerId;


    public  RemotePlayerSocketRouter(IEventSocket socket) {
        mSocket = socket;

        mPlayerByPlayerId = new HashMap<>();

        mSocket.on("move", new IEventSocket.OnRemoteEventReceivedListener() {
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

        mSocket.on("fire", new IEventSocket.OnRemoteEventReceivedListener() {
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
