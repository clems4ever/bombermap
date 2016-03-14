package com.game.wargame.Controller.Communication.RabbitMQ;

import com.game.wargame.Controller.Communication.ISocket;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.verify;

/**
 * Created by developer on 3/14/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class RabbitMQSocketTest {

    @Mock
    private RabbitMQConnectionThread mMockRabbitMQConnectionThread;

    @Mock private ISocket.OnRemoteEventReceivedListener mMockOnRemoteEventReceivedListener;

    @Test
    public void testRpcCall() throws JSONException {
        RabbitMQSocket rabbitMQSocket = new RabbitMQSocket(mMockRabbitMQConnectionThread, "exchange", "routing");

        JSONObject args = new JSONObject();
        args.put("arg1", "value");
        rabbitMQSocket.call("method", args, mMockOnRemoteEventReceivedListener);

        verify(mMockRabbitMQConnectionThread).call(anyString(), anyString(), eq(args), eq(mMockOnRemoteEventReceivedListener));
    }

    @Test
    public void testRpcCallWithoutRoutingKey() throws JSONException {
        RabbitMQSocket rabbitMQSocket = new RabbitMQSocket(mMockRabbitMQConnectionThread, "exchange");

        JSONObject args = new JSONObject();
        args.put("arg1", "value");
        rabbitMQSocket.call("method", args, mMockOnRemoteEventReceivedListener);

        verify(mMockRabbitMQConnectionThread).call(eq("exchange"), anyString(), eq(args), eq(mMockOnRemoteEventReceivedListener));
    }

    @Test
    public void testRpcCallWithoutExchangeAndRoutingKey() throws JSONException {
        RabbitMQSocket rabbitMQSocket = new RabbitMQSocket(mMockRabbitMQConnectionThread);

        JSONObject args = new JSONObject();
        args.put("arg1", "value");
        rabbitMQSocket.call("method", args, mMockOnRemoteEventReceivedListener);

        verify(mMockRabbitMQConnectionThread).call(eq(""), anyString(), eq(args), eq(mMockOnRemoteEventReceivedListener));
    }

    @Test
    public void testEventPublication() throws JSONException {
        RabbitMQSocket rabbitMQSocket = new RabbitMQSocket(mMockRabbitMQConnectionThread, "exchange", "routing");

        JSONObject content = new JSONObject();
        content.put("key1", "value");
        rabbitMQSocket.emit("event");

        JSONObject sentMessage = new JSONObject();
        sentMessage.put("ch", "event");

        verify(mMockRabbitMQConnectionThread).publish(eq("exchange"), eq("routing"), refEq(sentMessage));
    }

    @Test
    public void testEventPublicationWithContent() throws JSONException {
        RabbitMQSocket rabbitMQSocket = new RabbitMQSocket(mMockRabbitMQConnectionThread, "exchange", "routing");

        JSONObject content = new JSONObject();
        content.put("key1", "value");
        rabbitMQSocket.emit("event", content);

        JSONObject sentMessage = new JSONObject();
        sentMessage.put("co", content);
        sentMessage.put("ch", "event");

        verify(mMockRabbitMQConnectionThread).publish(eq("exchange"), eq("routing"), refEq(sentMessage));
    }

    @Test
    public void testSubscriptionToAnEvent() {
        RabbitMQSocket rabbitMQSocket = new RabbitMQSocket(mMockRabbitMQConnectionThread, "exchange", "routing");

        rabbitMQSocket.on("coucou", mMockOnRemoteEventReceivedListener);

        verify(mMockRabbitMQConnectionThread).subscribe(eq("coucou"), eq(mMockOnRemoteEventReceivedListener));
    }

    @Test
    public void testUnsubscribeFromEvent() {
        RabbitMQSocket rabbitMQSocket = new RabbitMQSocket(mMockRabbitMQConnectionThread, "exchange", "routing");

        rabbitMQSocket.off("coucou");

        verify(mMockRabbitMQConnectionThread).unsubscribe(eq("coucou"));
    }

}
