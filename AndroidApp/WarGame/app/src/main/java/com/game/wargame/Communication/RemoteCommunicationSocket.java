package com.game.wargame.Communication;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Ack;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONObject;

import java.net.URISyntaxException;

/**
 * Created by clement on 11/02/16.
 */
public class RemoteCommunicationSocket implements IRemoteCommunicationSocket {

    private final String SERVER_URI = "http://10.0.2.2:3001";

    private Socket mSocket;

    public RemoteCommunicationSocket() {
        try {
            mSocket = IO.socket(SERVER_URI);
        } catch (URISyntaxException e)
        {
        }
    }

    @Override
    public void connect() {
        mSocket.connect();
    }

    @Override
    public void disconnect() {
        mSocket.close();
        mSocket = null;
    }

    @Override
    public boolean isConnected() {
        return mSocket.connected();
    }

    @Override
    public void emit(String channel) {
        mSocket.emit(channel);
    }

    @Override
    public void emit(String channel, Ack ackListener) {
        mSocket.emit(channel, null, ackListener);
    }

    @Override
    public void emit(String channel, Object data) {
        mSocket.emit(channel, data);
    }

    @Override
    public void emit(String channel, Object data, Ack ack) {
        mSocket.emit(channel, data, ack);
    }

    @Override
    public void on(String channel, final IRemoteCommunicationSocket.OnRemoteEventReceivedListener onRemoteEventReceivedListener) {
        mSocket.on(channel, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                onRemoteEventReceivedListener.onRemoteEventReceived(args);
            }
        });
    }
}
