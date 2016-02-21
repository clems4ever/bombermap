package com.game.wargame.Controller.Communication;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public interface IEventSocket {

    public void connect();
    public void disconnect();

    public boolean isConnected();

    public JSONObject call(String method, JSONObject args) throws InterruptedException, JSONException, IOException;

    public void emit(String channel);
    public void emit(String channel, JSONObject data);

    public void on(String channel, IEventSocket.OnRemoteEventReceivedListener listener);
    public void off(String channel);

    public interface OnRemoteEventReceivedListener {
        public void onRemoteEventReceived(JSONObject message);
    }
}
