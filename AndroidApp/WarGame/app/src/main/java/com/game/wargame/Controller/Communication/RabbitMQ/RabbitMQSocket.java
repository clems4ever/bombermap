package com.game.wargame.Controller.Communication.RabbitMQ;

import com.game.wargame.Controller.Communication.IEventSocket;
import com.game.wargame.Controller.Communication.Socket;

import org.json.JSONException;
import org.json.JSONObject;

public class RabbitMQSocket extends Socket {

    public static final String CHANNEL_TAG = "ch";
    public static final String CONTENT_TAG = "co";

    private String mExchange;
    private String mRoutingKey;

    private RabbitMQConnectionManager mRMQConnectionManager;

    public RabbitMQSocket(RabbitMQConnectionManager connectionManager) {
        super(connectionManager);
        mRMQConnectionManager = connectionManager;
    }

    public RabbitMQSocket(RabbitMQConnectionManager connectionManager, String exchange) {
        super(connectionManager);
        mExchange = exchange;
        mRMQConnectionManager = connectionManager;
    }

    public RabbitMQSocket(RabbitMQConnectionManager connectionManager, String exchange, String routingKey) {
        super(connectionManager);
        mExchange = exchange;
        mRoutingKey = routingKey;
        mRMQConnectionManager = connectionManager;
    }

    @Override
    public void call(String method, JSONObject args, OnRemoteEventReceivedListener callback) {
        mRMQConnectionManager.getConnectionThread().call(mExchange, method, args, callback);
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

            mRMQConnectionManager.getConnectionThread().publish(mExchange, mRoutingKey, message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void on(String channel, OnRemoteEventReceivedListener listener) {
        mRMQConnectionManager.getConnectionThread().subscribe(channel, listener);
    }

    @Override
    public void off(String channel) {
        mRMQConnectionManager.getConnectionThread().unsubscribe(channel);
    }
}
