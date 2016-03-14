package com.game.wargame.Controller.Communication.RabbitMQ;

import com.game.wargame.Controller.Communication.ISocket;

import org.json.JSONException;
import org.json.JSONObject;

public class RabbitMQSocket implements ISocket {

    public static final String CHANNEL_TAG = "ch";
    public static final String CONTENT_TAG = "co";

    private String mExchange;
    private String mRoutingKey;

    RabbitMQConnectionThread mConnectionThread;

    public RabbitMQSocket(RabbitMQConnectionThread connectionThread) {
        mConnectionThread = connectionThread;
        mExchange = "";
        mRoutingKey = "";
    }

    public RabbitMQSocket(RabbitMQConnectionThread connectionThread, String exchange) {
        mExchange = exchange;
        mRoutingKey = "";
        mConnectionThread = connectionThread;
    }

    public RabbitMQSocket(RabbitMQConnectionThread connectionThread, String exchange, String routingKey) {
        mExchange = exchange;
        mRoutingKey = routingKey;
        mConnectionThread = connectionThread;
    }


    @Override
    public void call(String method, JSONObject args, OnRemoteEventReceivedListener callback) {
        mConnectionThread.call(mExchange, method, args, callback);
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

            mConnectionThread.publish(mExchange, mRoutingKey, message);
        } catch (JSONException e) {
        }
    }

    @Override
    public void on(String channel, OnRemoteEventReceivedListener listener) {
        mConnectionThread.subscribe(channel, listener);
    }

    @Override
    public void off(String channel) {
        mConnectionThread.unsubscribe(channel);
    }
}
