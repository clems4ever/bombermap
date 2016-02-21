package com.game.wargame.Controller.Communication;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by clement on 19/02/16.
 */
public class PlayerSocket {

    private IEventSocket mSocket;
    private String mPlayerId;

    // Listeners
    private OnMoveEventListener mOnMoveEventListener;
    private OnFireEventListener mOnFireEventListener;

    /**
     *
     * @param playerId
     * @param eventSocket
     */
    public PlayerSocket(String playerId, IEventSocket eventSocket) {
        mSocket = eventSocket;
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
            moveJsonObject.put("latitude", latitude);
            moveJsonObject.put("longitude", longitude);

            mSocket.emit("move", moveJsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setOnMoveEventListener(OnMoveEventListener onMoveEventListener) {
        mOnMoveEventListener = onMoveEventListener;

        mSocket.on("move", new IEventSocket.OnRemoteEventReceivedListener() {
            @Override
            public void onRemoteEventReceived(JSONObject message) {
                if(mOnMoveEventListener != null) {
                    try {
                        double latitude = message.getDouble("latitude");
                        double longitude = message.getDouble("longitude");
                        if(mOnMoveEventListener != null) {
                            mOnMoveEventListener.onMoveEvent(latitude, longitude);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
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

    public void setOnFireEventListener(OnFireEventListener onFireEventListener) {
        mOnFireEventListener = onFireEventListener;

        mSocket.on("fire", new IEventSocket.OnRemoteEventReceivedListener() {
            @Override
            public void onRemoteEventReceived(JSONObject message) {
                try {
                    double latitude = message.getDouble("latitude");
                    double longitude = message.getDouble("longitude");
                    double speed = message.getDouble("speed");

                    if(mOnFireEventListener != null) {
                        mOnFireEventListener.onFireEvent(latitude, longitude, speed);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
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
        mSocket.on("player_left", new IEventSocket.OnRemoteEventReceivedListener() {
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
