package com.game.wargame.Controller.Communication.RabbitMQ;

import com.game.wargame.Controller.Communication.IEventSocket;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.QueueingConsumer;

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

    private BlockingQueue<RabbitMQMessage> mQueue = new LinkedBlockingQueue<>();
    private BlockingQueue<JSONObject> mRpcReplies = new LinkedBlockingQueue<>();

    private Channel mRpcChannel;
    private String mRpcReplyQueueName;
    private QueueingConsumer mRpcConsumer;

    private boolean mStopThreadFlag = false;

    private Map<String, IEventSocket.OnRemoteEventReceivedListener> mListenerByChannel= new HashMap<>();


    public RabbitMQPublisherSubscriber(ConnectionFactory connectionFactory, String exchangeName) {
        mConnectionFactory = connectionFactory;
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
            channel.exchangeDeclare(mExchangeName, "direct", false, true, false, null);

            // declare the rpc reply queue
            mRpcChannel = connection.createChannel();
            mRpcReplyQueueName = mRpcChannel.queueDeclare().getQueue();
            mRpcConsumer = new QueueingConsumer(mRpcChannel);
            mRpcChannel.basicConsume(mRpcReplyQueueName, true, mRpcConsumer);

            setupConsumer(channel);
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
                message = mQueue.poll(3000, TimeUnit.MILLISECONDS);

                if(message != null) {
                    if(message.mRpc) {
                        AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
                                .correlationId(message.mCorrelationId)
                                .replyTo(mRpcReplyQueueName)
                                .build();

                        mRpcChannel.basicPublish("", message.mRpcMethod, props, message.mContent.toString().getBytes());

                        JSONObject response;
                        while (true) {
                            QueueingConsumer.Delivery delivery = mRpcConsumer.nextDelivery();
                            if (delivery.getProperties().getCorrelationId().equals(message.mCorrelationId)) {
                                String responseStr = new String(delivery.getBody());
                                response = new JSONObject(responseStr);
                                mRpcReplies.add(response);
                                break;
                            }
                        }
                    }
                    else {
                        channel.basicPublish(mExchangeName, "", null, message.toString().getBytes());
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
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

    public void publish(JSONObject data) {
        try {
            RabbitMQMessage message = new RabbitMQMessage();
            message.mContent = data;
            message.mRpc = false;

            mQueue.put(message);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public JSONObject call(String method, JSONObject args) throws IOException, InterruptedException, JSONException {
        JSONObject response = null;

        String corrId = java.util.UUID.randomUUID().toString();

        RabbitMQMessage message = new RabbitMQMessage();

        message.mContent = args;
        message.mCorrelationId = corrId;
        message.mRpc = true;
        message.mRpcMethod = method;

        mQueue.add(message);
        response = mRpcReplies.poll(5000, TimeUnit.MILLISECONDS);
        return response;
    }

    public void subscribe(String channel, IEventSocket.OnRemoteEventReceivedListener listener) {
        mListenerByChannel.put(channel, listener);
    }

    public void unsubscribe(String channel) {
        mListenerByChannel.remove(channel);
    }
}
