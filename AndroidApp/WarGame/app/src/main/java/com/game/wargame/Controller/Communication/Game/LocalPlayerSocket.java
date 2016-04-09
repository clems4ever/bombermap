package com.game.wargame.Controller.Communication.Game;

import com.game.wargame.Controller.Communication.ISocket;
import com.game.wargame.Controller.Communication.ISocketFactory;

import org.json.JSONException;
import org.json.JSONObject;

public class LocalPlayerSocket extends PlayerSocket {

    private ISocket mSocket;

    /**
     * @param playerId
     * @param eventSocket
     */
    public LocalPlayerSocket(String playerId, ISocket eventSocket) {
        super(playerId);

        mSocket = eventSocket;
    }

    private JSONObject buildClientJson() throws JSONException {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("player_id", mPlayerId);

        return jsonObject;
    }

    public void move(double latitude, double longitude) {
        try {
            JSONObject moveJsonObject = buildClientJson();
            moveJsonObject.put("lat", latitude);
            moveJsonObject.put("long", longitude);

            mSocket.emit("move", moveJsonObject);

        } catch (JSONException e) {
        }
    }

    public void fire(double latitude, double longitude, double time) {
        try {
            JSONObject fireJsonObject = buildClientJson();

            fireJsonObject.put("lat", latitude);
            fireJsonObject.put("long", longitude);
            fireJsonObject.put("time", time);

            mSocket.emit("fire", fireJsonObject);
        } catch (JSONException e) {
        }
    }

    public void die(String playerId, String killerId, double time) {
        try {
            JSONObject diedJsonObject = buildClientJson();

            diedJsonObject.put("player_id", playerId);
            diedJsonObject.put("killer_id", killerId);
            diedJsonObject.put("time", time);

            mSocket.emit("died", diedJsonObject);
        } catch (JSONException e) {
        }
    }

    public void respawn(double time) {
        try {
            JSONObject respawnJson = buildClientJson();
            respawnJson.put("time", time);
            mSocket.emit("respawn", respawnJson);
        } catch (JSONException e) {
        }
    }

    public void leave() {
        try {
            JSONObject data = buildClientJson();

            mSocket.emit("player_leave", data);
        } catch (JSONException e) {
        }
    }

    public void gameStart(double time) {
        try {
            JSONObject data = buildClientJson();
            data.put("time", time);
            mSocket.emit("game_start_sync", data);
        } catch (JSONException e) {
        }
    }
}
