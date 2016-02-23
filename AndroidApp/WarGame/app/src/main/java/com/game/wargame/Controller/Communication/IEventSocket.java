package com.game.wargame.Controller.Communication;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public interface IEventSocket {

    public void connect();
    public void disconnect();

    public void setOnDisconnected(OnDisconnectedListener onDisconnectedListener);

    public boolean isConnected();

    public void call(String method, JSONObject args, OnRemoteEventReceivedListener listener);

    public void emit(String channel);
    public void emit(String channel, JSONObject data);

    public void on(String channel, IEventSocket.OnRemoteEventReceivedListener listener);
    public void off(String channel);

    public interface OnRemoteEventReceivedListener {
        public void onRemoteEventReceived(JSONObject message);
    }

    public interface OnDisconnectedListener {
        public void onDisconnected();
    }
}
