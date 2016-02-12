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
public class RemoteCommunicationSocket {

    private final String SERVER_URI = "http://10.0.2.2:3001";

    private Socket mSocket;

    public RemoteCommunicationSocket() {
        try {
            mSocket = IO.socket(SERVER_URI);
        } catch (URISyntaxException e)
        {
        }
    }

    public void connect() {
        mSocket.connect();
    }

    public void disconnect() {
        mSocket.close();
        mSocket = null;
    }

    public void emit(String channel) {
        mSocket.emit(channel);
    }

    public void emit(String channel, Ack ackListener) {
        mSocket.emit(channel, null, ackListener);
    }

    public void emit(String channel, Object data) {
        mSocket.emit(channel, data);
    }

    public void emit(String channel, Object data, Ack ack) {
        mSocket.emit(channel, data, ack);
    }

    public void on(String channel, final OnRemoteEventReceivedListener onRemoteEventReceivedListener) {
        mSocket.on(channel, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                onRemoteEventReceivedListener.onRemoteEventReceived(args);
            }
        });
    }

    public interface OnRemoteEventReceivedListener {
        public void onRemoteEventReceived(Object... args);
    }
}
