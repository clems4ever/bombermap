package com.game.wargame.Controller.Communication.RabbitMQ;

import com.game.wargame.Controller.Communication.IConnectionManager;
import com.game.wargame.Controller.Communication.ISocketFactory;

public class RabbitMQConnectionManager implements IConnectionManager {

    private RabbitMQConnectionThread mConnectionThread;
    private RabbitMQSocketFactory mSocketFactory;
    private String mVirtualHost;


    public RabbitMQConnectionManager(String virtualHost) {
        mVirtualHost = virtualHost;
    }

    @Override
    public void connect(String host) {
        mConnectionThread = new RabbitMQConnectionThread(host, mVirtualHost);
        mConnectionThread.start();
        mSocketFactory = new RabbitMQSocketFactory(mConnectionThread);
    }

    @Override
    public void initSocketFactory() {
        mSocketFactory.initRemotePlayersSocket();
    }

    @Override
    public void disconnect() {
        mConnectionThread.interrupt();
        mSocketFactory = null;
        mConnectionThread = null;
    }

    @Override
    public void setOnDisconnected(IConnectionManager.OnDisconnectedListener onDisconnectedListener) {
        mConnectionThread.setOnDisconnectedListener(onDisconnectedListener);
    }

    @Override
    public boolean isConnected() {
        return mConnectionThread.isConnected();
    }

    @Override
    public void freeze() {

    }

    @Override
    public void unfreeze() {

    }

    @Override
    public ISocketFactory getSocketFactory() {
        return mSocketFactory;
    }

    @Override
    public void clear() {
        mConnectionThread.clear();
    }

    public interface OnDisconnectedListener {
        public void onDisconnected();
    }
}
