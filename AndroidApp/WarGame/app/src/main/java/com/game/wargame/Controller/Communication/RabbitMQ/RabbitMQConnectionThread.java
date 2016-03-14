package com.game.wargame.Controller.Communication.RabbitMQ;

import android.util.Log;

import com.game.wargame.Controller.Communication.IConnectionManager;
import com.game.wargame.Controller.Communication.ISocket;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AlreadyClosedException;
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

public class RabbitMQConnectionThread extends Thread {

    private ConnectionFactory mConnectionFactory;
    private Connection mConnection;

    private BlockingQueue<RabbitMQMessage> mQueue = new LinkedBlockingQueue<>();

    private boolean mStopThreadFlag = false;
    private IConnectionManager.OnDisconnectedListener mOnDisconnectedListener;

    private Map<String, ISocket.OnRemoteEventReceivedListener> mRpcRepliesCallback = new HashMap<>();
    private Map<String, ISocket.OnRemoteEventReceivedListener> mListenerByChannel= new HashMap<>();

    public RabbitMQConnectionThread(String host) {
        mConnectionFactory = new ConnectionFactory();
        mConnectionFactory.setAutomaticRecoveryEnabled(false);
        mConnectionFactory.setHost(host);
        mConnectionFactory.setUsername("player");
        mConnectionFactory.setPassword("player");
        mConnectionFactory.setHandshakeTimeout(600000);
        mConnectionFactory.setRequestedHeartbeat(240);
    }

    public void setOnDisconnectedListener(IConnectionManager.OnDisconnectedListener onDisconnectedListener) {
        mOnDisconnectedListener = onDisconnectedListener;
    }

    public boolean isConnected() {
        return mConnection != null && mConnection.isOpen();
    }

    @Override
    public void run() {
        Channel channel = null;
        boolean error = false;
        String clientQueueName = null;

        try {
            mConnection = mConnectionFactory.newConnection();
            channel = mConnection.createChannel();
            clientQueueName = channel.queueDeclare("", false, false, true, null).getQueue();

            setupConsumer(channel, clientQueueName);
        } catch (IOException e) {
            error = true;
            e.printStackTrace();
        } catch (TimeoutException e) {
            error = true;
            e.printStackTrace();
        }

        RabbitMQMessage message;
        String content;
        AMQP.BasicProperties properties = null;

        while (!mStopThreadFlag && !error) {
            try {
                message = mQueue.poll(1000, TimeUnit.MILLISECONDS);

                if(message != null) {
                    if(message.mRpc) {
                        properties = new AMQP.BasicProperties.Builder()
                                .correlationId(message.mCorrelationId)
                                .replyTo(clientQueueName)
                                .build();

                    }
                    else {
                        properties = null;
                    }

                    if(message.mContent != null) {
                        content = message.mContent.toString();
                    }
                    else {
                        content = new String();
                    }
                    channel.basicPublish(message.mExchange, message.mRoutingKey, properties, content.getBytes());
                }
            } catch (InterruptedException e) {
                error = true;
            } catch (IOException e) {
                error = true;
                e.printStackTrace();
            } catch (AlreadyClosedException e) {
                error = true;
                e.printStackTrace();
            }
        }

        try {
            if(channel != null) {
                if(!error) {
                    channel.queueDelete(clientQueueName);
                }
                channel.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (AlreadyClosedException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

        if(mConnection != null) {
            try {
                mConnection.close();
                mConnection = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(mOnDisconnectedListener != null) {
            mOnDisconnectedListener.onDisconnected();
        }
    }

    private void setupConsumer(Channel channel, String queueName) {
        try {
            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope,
                                           AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message = new String(body, "UTF-8");
                    try {
                        JSONObject messageJson = new JSONObject(message);
                        routeMessage(messageJson, properties);
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

    private void routeMessage(JSONObject content, AMQP.BasicProperties properties) throws JSONException {
        String correlationId = properties.getCorrelationId();
        ISocket.OnRemoteEventReceivedListener listener = mRpcRepliesCallback.get(correlationId);

        if(listener != null) {
            listener.onRemoteEventReceived(content);
            mRpcRepliesCallback.remove(correlationId);
        }
        else {
            String channel = content.optString(RabbitMQSocket.CHANNEL_TAG);
            JSONObject data = content.optJSONObject(RabbitMQSocket.CONTENT_TAG);

            listener = mListenerByChannel.get(channel);
            if(listener != null) {
                listener.onRemoteEventReceived(data);
            }
            else {
                Log.v("[RabbitMQThread]", "Listener not found for channel :" + channel);
            }
        }
    }

    public void publish(String exchangeName, String routingKey, JSONObject data) {
        try {
            RabbitMQMessage message = new RabbitMQMessage();
            message.mContent = data;
            message.mRpc = false;
            message.mRoutingKey = routingKey;
            message.mExchange = exchangeName;

            mQueue.put(message);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void call(String exchangeName, String method, JSONObject args, ISocket.OnRemoteEventReceivedListener listener) {
        String corrId = java.util.UUID.randomUUID().toString();

        RabbitMQMessage message = new RabbitMQMessage();

        message.mContent = args;
        message.mCorrelationId = corrId;
        message.mRpc = true;
        message.mRoutingKey = method;
        message.mExchange = exchangeName;

        mRpcRepliesCallback.put(corrId, listener);
        mQueue.add(message);
    }

    public void subscribe(String channel, ISocket.OnRemoteEventReceivedListener listener) {
        mListenerByChannel.put(channel, listener);
    }

    public void unsubscribe(String channel) {
        mListenerByChannel.remove(channel);
    }

    public void clear() {
        mListenerByChannel.clear();
        mRpcRepliesCallback.clear();
    }
}
