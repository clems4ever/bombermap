package com.game.wargame.Controller.Communication.RabbitMQ;

import com.game.wargame.Controller.Communication.IEventSocket;
import com.rabbitmq.client.ConnectionFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class RabbitMQSocket implements IEventSocket {

    public static final String CHANNEL_TAG = "ch";
    public static final String CONTENT_TAG = "co";

    private String mGameId;
    private String mHost;

    private RabbitMQPublisherSubscriber mPublisherSubscriber;

    public RabbitMQSocket(String host, String gameId) {
        mHost = host;
        mGameId = gameId;
    }

    @Override
    public void connect() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setAutomaticRecoveryEnabled(false);
        factory.setHost(mHost);
        factory.setHandshakeTimeout(600000);
        factory.setRequestedHeartbeat(240);

        mPublisherSubscriber = new RabbitMQPublisherSubscriber(factory, mGameId + "_game_room");

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
    public JSONObject call(String method, JSONObject args) throws InterruptedException, JSONException, IOException {
        return mPublisherSubscriber.call(mGameId + "_" + method, args);
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
