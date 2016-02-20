package com.game.wargame.Communication;

import com.github.nkzawa.socketio.client.Ack;

import org.json.JSONObject;

import java.util.Objects;

/**
 * Created by clement on 12/02/16.
 */
public interface IRemoteCommunicationSocket {

    public void connect(String gameRoom);
    public void disconnect();

    public boolean isConnected();

    public void emit(String channel);
    public void emit(String channel, JSONObject data);

    public void on(String channel, IRemoteCommunicationSocket.OnRemoteEventReceivedListener listener);
    public void off(String channel);

    public interface OnRemoteEventReceivedListener {
        public void onRemoteEventReceived(JSONObject message);
    }
}
