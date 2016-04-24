package com.game.wargame.Controller.Communication.RabbitMQ;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by clement on 24/04/16.
 */
public class RabbitMQMessageLogger {

    private JSONArray mMessages;

    public RabbitMQMessageLogger() {
        mMessages = new JSONArray();
    }

    public void log(JSONObject message) {
        mMessages.put(message.toString());
    }

    public String dump() {
        return mMessages.toString();
    }
}
