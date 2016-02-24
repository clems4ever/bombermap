package com.game.wargame.Mock;

import com.game.wargame.Controller.Communication.IEventSocket;

import org.json.JSONObject;

/**
 * Created by clement on 20/02/16.
 */
public class MockSocket implements IEventSocket {

    @Override
    public void connect() {

    }

    @Override
    public void setOnDisconnected(OnDisconnectedListener onDisconnectedListener)
    {}

    @Override
    public void disconnect() {

    }

    @Override
    public void call(String method, JSONObject args, OnRemoteEventReceivedListener listener)
    {}

    @Override
    public boolean isConnected() {
        return true;
    }

    @Override
    public void emit(String channel) {

    }

    @Override
    public void emit(String channel, JSONObject data) {

    }

    @Override
    public void on(String channel, OnRemoteEventReceivedListener listener) {

    }

    @Override
    public void off(String channel) {

    }
}
