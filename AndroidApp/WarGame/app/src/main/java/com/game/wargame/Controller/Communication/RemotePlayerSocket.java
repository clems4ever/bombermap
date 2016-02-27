package com.game.wargame.Controller.Communication;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by clement on 26/02/16.
 */
public class RemotePlayerSocket extends PlayerSocket {

    // Listeners
    private OnMoveEventListener mOnMoveEventListener;
    private OnFireEventListener mOnFireEventListener;

    /**
     * @param playerId
     * @param eventSocket
     */
    public RemotePlayerSocket(String playerId, IEventSocket eventSocket) {
        super(playerId, eventSocket);
    }

    public void setOnMoveEventListener(OnMoveEventListener onMoveEventListener) {
        mOnMoveEventListener = onMoveEventListener;

        mSocket.on("move", new IEventSocket.OnRemoteEventReceivedListener() {
            @Override
            public void onRemoteEventReceived(JSONObject message) {
                if (mOnMoveEventListener != null) {
                    try {
                        double latitude = message.getDouble("latitude");
                        double longitude = message.getDouble("longitude");
                        if (mOnMoveEventListener != null) {
                            mOnMoveEventListener.onMoveEvent(latitude, longitude);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
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

    public interface OnMoveEventListener {
        public void onMoveEvent(double latitude, double longitude);
    }

    public interface OnFireEventListener {
        public void onFireEvent(double latitude, double longitude, double velocity);
    }
}
