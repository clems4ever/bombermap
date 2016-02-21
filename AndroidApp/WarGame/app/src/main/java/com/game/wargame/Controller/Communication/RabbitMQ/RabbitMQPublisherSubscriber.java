package com.game.wargame.Controller.Communication.RabbitMQ;

import com.game.wargame.Controller.Communication.IEventSocket;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class RabbitMQPublisherSubscriber extends Thread {

    private ConnectionFactory mConnectionFactory;
    private String mExchangeName;

    private BlockingQueue<JSONObject> mQueue;

    private boolean mStopThreadFlag = false;

    private Map<String, IEventSocket.OnRemoteEventReceivedListener> mListenerByChannel= new HashMap<>();


    public RabbitMQPublisherSubscriber(ConnectionFactory connectionFactory, String exchangeName) {
        mConnectionFactory = connectionFactory;
        mQueue = new LinkedBlockingQueue<>();

        mExchangeName = exchangeName;
    }

    @Override
    public void run() {
        Channel channel = null;
        Connection connection = null;
        boolean error = false;
        try {
            connection = mConnectionFactory.newConnection();
            channel = connection.createChannel();
            channel.exchangeDeclare(mExchangeName, "fanout", false, true, false, null);

            setupConsumer(channel);
        } catch (IOException e) {
            error = true;
            e.printStackTrace();
        } catch (TimeoutException e) {
            error = true;
            e.printStackTrace();
        }

        JSONObject message;
        while (!mStopThreadFlag && !error) {
            try {
                message = mQueue.poll(2000, TimeUnit.MILLISECONDS);
                if(message != null) {
                    channel.basicPublish(mExchangeName, "", null, message.toString().getBytes());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (!error) {
            try {
                channel.exchangeDelete(mExchangeName, true);
                channel.close();
                connection.close();
            } catch (IOException e) {

            } catch (TimeoutException e) {
                e.printStackTrace();
            }
        }
    }

    private void setupConsumer(Channel channel) {
        try {
            String queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, mExchangeName, "");
            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope,
                                           AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message = new String(body, "UTF-8");
                    try {
                        JSONObject messageJson = new JSONObject(message);

                        String channel = messageJson.optString(RabbitMQSocket.CHANNEL_TAG);
                        JSONObject content = messageJson.optJSONObject(RabbitMQSocket.CONTENT_TAG);

                        IEventSocket.OnRemoteEventReceivedListener listener = mListenerByChannel.get(channel);
                        if(listener != null) {
                            listener.onRemoteEventReceived(content);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
            channel.basicConsume(queueName, true, consumer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        mStopThreadFlag = true;
    }

    public void publish(JSONObject message) {
        try {
            mQueue.put(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void subscribe(String channel, IEventSocket.OnRemoteEventReceivedListener listener) {
        mListenerByChannel.put(channel, listener);
    }

    public void unsubscribe(String channel) {
        mListenerByChannel.remove(channel);
    }
}
