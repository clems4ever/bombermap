package com.game.wargame.Controller.Communication;

import org.json.JSONObject;

public interface ISocket {

    public void call(String method, JSONObject args, OnRemoteEventReceivedListener listener);

    public void emit(String channel);
    public void emit(String channel, JSONObject data);

    public void on(String channel, ISocket.OnRemoteEventReceivedListener listener);
    public void off(String channel);

    public interface OnRemoteEventReceivedListener {
        public void onRemoteEventReceived(JSONObject message);
    }
}
