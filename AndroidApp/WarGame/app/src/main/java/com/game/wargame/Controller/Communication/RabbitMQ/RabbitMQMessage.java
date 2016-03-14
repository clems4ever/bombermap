package com.game.wargame.Controller.Communication.RabbitMQ;

import com.rabbitmq.client.AMQP;

import org.json.JSONObject;

/**
 * Created by clement on 21/02/16.
 */
public class RabbitMQMessage {

    public static final int RECEIVE = 0;
    public static final int SEND = 1;

    public int mType;
    public boolean mRpc;
    public String mRoutingKey;
    public String mExchange;
    public JSONObject mContent;

    public String mCorrelationId;
    public AMQP.BasicProperties mBasicProperties;
}
