package com.game.wargame.Controller.Communication.RabbitMQ;

import com.game.wargame.Controller.Communication.IEventSocket;
import com.rabbitmq.client.ConnectionFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class RabbitMQSocket implements IEventSocket {

    public static final String CHANNEL_TAG = "ch";
    public static final String CONTENT_TAG = "co";

    private String mHost;

    private RabbitMQPublisherSubscriber mPublisherSubscriber;

    public RabbitMQSocket(String host, String gameId) {
        mHost = host;

        ConnectionFactory factory = new ConnectionFactory();
        factory.setAutomaticRecoveryEnabled(false);
        factory.setHost(mHost);
        factory.setHandshakeTimeout(600000);
        factory.setRequestedHeartbeat(240);

        mPublisherSubscriber = new RabbitMQPublisherSubscriber(factory, gameId + "_game_room");
    }

    @Override
    public void connect() {
        mPublisherSubscriber.start();
    }

    @Override
    public void disconnect() {
        mPublisherSubscriber.disconnect();
    }

    @Override
    public void setOnDisconnected(OnDisconnectedListener onDisconnectedListener) {
        mPublisherSubscriber.setOnDisconnectedListener(onDisconnectedListener);
    }

    @Override
    public boolean isConnected() {
        return true;
    }

    @Override
    public void call(String method, JSONObject args, OnRemoteEventReceivedListener callback) {
        mPublisherSubscriber.call(method, args, callback);
    }

    @Override
    public void emit(String channel) {
        emit(channel, null);
    }

    @Override
    public void emit(String channel, JSONObject data) {
        JSONObject message = new JSONObject();

        try {
            message.put(CHANNEL_TAG, channel);
            message.put(CONTENT_TAG, data);

            mPublisherSubscriber.publish(message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void on(String channel, OnRemoteEventReceivedListener listener) {
        mPublisherSubscriber.subscribe(channel, listener);
    }

    @Override
    public void off(String channel) {
        mPublisherSubscriber.unsubscribe(channel);
    }
}
