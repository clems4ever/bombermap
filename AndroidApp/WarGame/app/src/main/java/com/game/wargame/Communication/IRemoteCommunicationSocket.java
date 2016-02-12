package com.game.wargame.Communication;

import com.github.nkzawa.socketio.client.Ack;

import java.util.Objects;

/**
 * Created by clement on 12/02/16.
 */
public interface IRemoteCommunicationSocket {

    public void connect();
    public void disconnect();

    public boolean isConnected();

    public void emit(String channel);
    public void emit(String channel, Ack ackListener);
    public void emit(String channel, Object data);
    public void emit(String channel, Object data, Ack ackListener);

    public void on(String channel, IRemoteCommunicationSocket.OnRemoteEventReceivedListener listener);

    public interface OnRemoteEventReceivedListener {
        public void onRemoteEventReceived(Object... args);
    }
}
