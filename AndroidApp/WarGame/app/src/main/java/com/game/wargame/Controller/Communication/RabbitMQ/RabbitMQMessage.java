package com.game.wargame.Controller.Communication.RabbitMQ;

import org.json.JSONObject;

/**
 * Created by clement on 21/02/16.
 */
public class RabbitMQMessage {

    public boolean mRpc;
    public String mRpcMethod;
    public JSONObject mContent;

    public String mCorrelationId;
}
