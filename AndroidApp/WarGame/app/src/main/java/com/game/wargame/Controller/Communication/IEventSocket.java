package com.game.wargame.Controller.Communication;

import org.json.JSONObject;

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
