package com.game.wargame.Controller.Communication.Game;

import com.game.wargame.Controller.Communication.ISocket;
import com.game.wargame.Controller.Communication.ISocketFactory;

import org.json.JSONException;
import org.json.JSONObject;

public class LocalPlayerSocket extends PlayerSocket {

    private ISocket mSocket;
    private ISocketFactory mSocketFactory;

    /**
     * @param playerId
     * @param eventSocket
     */
    public LocalPlayerSocket(String playerId, ISocket eventSocket, ISocketFactory socketFactory) {
        super(playerId);

        mSocket = eventSocket;
        mSocketFactory = socketFactory;
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
            e.printStackTrace();
        }
    }

    public void fire(double latitude, double longitude, double velocity) {

        try {
            JSONObject fireJsonObject = buildClientJson();

            fireJsonObject.put("lat", latitude);
            fireJsonObject.put("long", longitude);
            fireJsonObject.put("speed", velocity);

            mSocket.emit("fire", fireJsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void join() {
        try {
            JSONObject data = buildClientJson();
            mSocket.emit("player_join", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void joinAck() {
        try {
            JSONObject data = buildClientJson();
            mSocket.emit("player_join_ack", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void leave() {
        try {
            JSONObject data = buildClientJson();
            mSocket.emit("player_leave", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public LocalPlayerSocket to(RemotePlayerSocket playerSocket) {
        return new LocalPlayerSocket(mPlayerId, mSocketFactory.buildDirectPeerSocket("abc", playerSocket), mSocketFactory);
    }
}