package com.game.wargame.Controller.Communication.RabbitMQ;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static junit.framework.Assert.assertEquals;

/**
 * Created by clement on 25/04/16.
 */

@RunWith(MockitoJUnitRunner.class)
public class RabbitMQMessageLoggerTest {

    @Test
    public void test_logger_produces_json_compatible_string() throws JSONException {

        RabbitMQMessageLogger logger = new RabbitMQMessageLogger();

        JSONObject obj1 = new JSONObject();
        obj1.put("a1", "aaa");
        obj1.put("a2", "bbb");

        JSONObject obj2 = new JSONObject();

        obj2.put("b1", 3);
        obj2.put("b2", "abc");

        logger.log(obj1);
        logger.log(obj2);

        String s = logger.dump();

        assertEquals(s, "[{\"ac1\":\"aaa\",\"a2\":\"bbb\"},{\"b1\":3,\"b2\":\"abc\"}]");
    }

}
