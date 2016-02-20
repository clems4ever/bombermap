package com.game.wargame.Communication;

import org.json.JSONObject;

/**
 * Created by clement on 12/02/16.
 */
public interface IEventSocket {

    public void connect(String gameRoom);
    public void disconnect();

    public boolean isConnected();

    public void emit(String channel);
    public void emit(String channel, JSONObject data);

    public void on(String channel, IEventSocket.OnRemoteEventReceivedListener listener);
    public void off(String channel);

    public interface OnRemoteEventReceivedListener {
        public void onRemoteEventReceived(JSONObject message);
    }
}
