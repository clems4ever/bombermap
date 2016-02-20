package com.game.wargame.Communication;

import com.game.wargame.Communication.SocketIO.RemoteCommunicationSocket;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by clement on 19/02/16.
 */
public class PlayerSocket {

    private IRemoteCommunicationSocket mSocket;
    private String mPlayerId;

    // Listeners
    private OnMoveEventListener mOnMoveEventListener;
    private OnFireEventListener mOnFireEventListener;

    /**
     *
     * @param playerId
     * @param remoteCommunicationSocket
     */
    public PlayerSocket(String playerId, IRemoteCommunicationSocket remoteCommunicationSocket) {
        mSocket = remoteCommunicationSocket;
        mPlayerId = playerId;
    }

    public String getPlayerId() {
        return mPlayerId;
    }

    private JSONObject buildClientJson() throws JSONException {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("client_id", mPlayerId);

        return jsonObject;
    }

    public void move(double latitude, double longitude) {
        try {
            JSONObject moveJsonObject = buildClientJson();
            moveJsonObject.put("x", latitude);
            moveJsonObject.put("y", longitude);

            mSocket.emit("move", moveJsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setOnMoveEventListener(OnMoveEventListener onMoveEventListener) {
        mOnMoveEventListener = onMoveEventListener;
    }

    public void fire(double latitude, double longitude, double velocity) {

        try {
            JSONObject fireJsonObject = buildClientJson();

            fireJsonObject.put("latitude", latitude);
            fireJsonObject.put("longitude", longitude);
            fireJsonObject.put("velocity", velocity);

            mSocket.emit("fire", fireJsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setOnFireEventListener(OnFireEventListener onFireEventListener) {
        mOnFireEventListener = onFireEventListener;
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

    public void setOnPlayerLeftListener(final OnPlayerLeftListener onPlayerLeftListener) {
        mSocket.on("player_left", new RemoteCommunicationSocket.OnRemoteEventReceivedListener() {
            @Override
            public void onRemoteEventReceived(JSONObject data) {
                try {
                    String playerId = data.getString("player_id");
                    onPlayerLeftListener.onPlayerLeft(playerId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public interface OnPlayerLeftListener {
        public void onPlayerLeft(String playerId);
    }

    public interface OnMoveEventListener {
        public void onMoveEvent(double latitude, double longitude);
    }

    public interface OnFireEventListener {
        public void onFireEvent(double latitude, double longitude, double velocity);
    }
}
