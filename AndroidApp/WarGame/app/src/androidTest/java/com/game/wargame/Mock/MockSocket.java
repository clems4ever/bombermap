package com.game.wargame.Mock;

import com.game.wargame.Communication.IEventSocket;

import org.json.JSONObject;

/**
 * Created by clement on 20/02/16.
 */
public class MockSocket implements IEventSocket {
    @Override
    public void connect(String gameRoom) {

    }

    @Override
    public void disconnect() {

    }

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
