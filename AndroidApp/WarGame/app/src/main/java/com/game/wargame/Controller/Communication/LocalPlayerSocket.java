package com.game.wargame.Controller.Communication;

import org.json.JSONException;
import org.json.JSONObject;

public class LocalPlayerSocket extends PlayerSocket {
    /**
     * @param playerId
     * @param eventSocket
     */
    public LocalPlayerSocket(String playerId, IEventSocket eventSocket) {
        super(playerId, eventSocket);
    }

    private JSONObject buildClientJson() throws JSONException {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("client_id", mPlayerId);

        return jsonObject;
    }

    public void move(double latitude, double longitude) {
        try {
            JSONObject moveJsonObject = buildClientJson();
            moveJsonObject.put("latitude", latitude);
            moveJsonObject.put("longitude", longitude);

            mSocket.emit("move", moveJsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void fire(double latitude, double longitude, double velocity) {

        try {
            JSONObject fireJsonObject = buildClientJson();

            fireJsonObject.put("latitude", latitude);
            fireJsonObject.put("longitude", longitude);
            fireJsonObject.put("speed", velocity);

            mSocket.emit("fire", fireJsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void joinGame(String gameId, final String username) {
        JSONObject data = new JSONObject();
        try {
            data.put("game_id", gameId);
            data.put("username", username);

            mSocket.emit("player_join", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
