package com.game.wargame.Communication.RabbitMQ;

import com.game.wargame.Communication.IEventSocket;
import com.rabbitmq.client.ConnectionFactory;

import org.json.JSONException;
import org.json.JSONObject;

public class RabbitMQSocket implements IEventSocket {

    public static final String CHANNEL_TAG = "ch";
    public static final String CONTENT_TAG = "co";

    private String mExchangeName;
    private String mHost;

    private RabbitMQPublisherSubscriber mPublisherSubscriber;

    public RabbitMQSocket(String host) {
        mHost = host;
    }

    @Override
    public void connect(String gameRoom) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setAutomaticRecoveryEnabled(false);
        factory.setHost(mHost);
        //factory.setHandshakeTimeout(0);
        factory.setRequestedHeartbeat(60);

        mPublisherSubscriber = new RabbitMQPublisherSubscriber(factory, gameRoom);

        mPublisherSubscriber.start();
    }

    @Override
    public void disconnect() {
        mPublisherSubscriber.disconnect();
    }

    @Override
    public boolean isConnected() {
        return true;
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
