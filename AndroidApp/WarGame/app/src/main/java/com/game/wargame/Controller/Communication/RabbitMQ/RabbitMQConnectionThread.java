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
    private String mClientQueueName;
    private Channel mChannel;

    private BlockingQueue<RabbitMQMessage> mCommandQueue;
    private SocketBuffer mSocketBuffer;

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

        mCommandQueue = new LinkedBlockingQueue<>();
        mSocketBuffer = new SocketBuffer(mCommandQueue);
    }

    public void setOnDisconnectedListener(IConnectionManager.OnDisconnectedListener onDisconnectedListener) {
        mOnDisconnectedListener = onDisconnectedListener;
    }

    public boolean isConnected() {
        return mConnection != null && mConnection.isOpen();
    }

    @Override
    public void run() {
        boolean error = false;
        String clientQueueName = null;

        try {
            mConnection = mConnectionFactory.newConnection();
            mChannel = mConnection.createChannel();
            mClientQueueName = mChannel.queueDeclare("", false, false, true, null).getQueue();

            setupConsumer(mChannel, mClientQueueName);
        } catch (IOException e) {
            error = true;
            e.printStackTrace();
        } catch (TimeoutException e) {
            error = true;
            e.printStackTrace();
        }

        RabbitMQMessage message;

        while (!mStopThreadFlag && !error) {
            try {
                message = mCommandQueue.poll(1000, TimeUnit.MILLISECONDS);

                if(message != null) {
                    if (message.mType == RabbitMQMessage.SEND) {
                        sendMessage(message);
                    } else if (message.mType == RabbitMQMessage.RECEIVE) {
                        receiveMessage(message);
                    }
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
            if(mChannel != null) {
                if(!error) {
                    mChannel.queueDelete(clientQueueName);
                }
                mChannel.close();
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

    private void sendMessage(RabbitMQMessage message) throws IOException {
        AMQP.BasicProperties properties = null;
        String content;

        if(message != null) {
            if(message.mRpc) {
                properties = new AMQP.BasicProperties.Builder()
                        .correlationId(message.mCorrelationId)
                        .replyTo(mClientQueueName)
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
            mChannel.basicPublish(message.mExchange, message.mRoutingKey, properties, content.getBytes());
        }
    }

    private void receiveMessage(RabbitMQMessage message) {
        JSONObject content = message.mContent;
        AMQP.BasicProperties properties = message.mBasicProperties;

        try {
            routeMessage(content, properties);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setupConsumer(Channel channel, String queueName) {
        try {
            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope,
                                           AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String content = new String(body, "UTF-8");
                    treatReceivedMessage(content, properties);
                }
            };
            channel.basicConsume(queueName, true, consumer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void treatReceivedMessage(String content, AMQP.BasicProperties properties) {
        try {
            RabbitMQMessage rabbitMQMessage = new RabbitMQMessage();

            JSONObject messageJson = new JSONObject(content);
            rabbitMQMessage.mType = RabbitMQMessage.RECEIVE;
            rabbitMQMessage.mContent = messageJson;
            rabbitMQMessage.mBasicProperties = properties;
            mSocketBuffer.consume(rabbitMQMessage);
        } catch (JSONException e) {
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
        RabbitMQMessage message = new RabbitMQMessage();
        message.mType = RabbitMQMessage.SEND;
        message.mContent = data;
        message.mRpc = false;
        message.mRoutingKey = routingKey;
        message.mExchange = exchangeName;

        mSocketBuffer.consume(message);
    }

    public void call(String exchangeName, String method, JSONObject args, ISocket.OnRemoteEventReceivedListener listener) {
        String corrId = java.util.UUID.randomUUID().toString();

        RabbitMQMessage message = new RabbitMQMessage();
        message.mType = RabbitMQMessage.SEND;
        message.mContent = args;
        message.mCorrelationId = corrId;
        message.mRpc = true;
        message.mRoutingKey = method;
        message.mExchange = exchangeName;

        mRpcRepliesCallback.put(corrId, listener);
        mSocketBuffer.consume(message);
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

    public void freeze() {
        mSocketBuffer.freeze();
    }

    public void unfreeze() {
        mSocketBuffer.unfreeze();
    }
}
