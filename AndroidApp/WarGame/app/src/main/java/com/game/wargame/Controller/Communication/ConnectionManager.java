package com.game.wargame.Controller.Communication;

import com.game.wargame.Controller.Communication.RabbitMQ.RabbitMQConnectionManager;

public class ConnectionManager {

    private static int mCount = 0;
    private static IConnectionManager mConnectionManagerImpl = new RabbitMQConnectionManager();

    private ConnectionManager() {
    }

    public static IConnectionManager getInstance() {
        return mConnectionManagerImpl;
    }

    public static IConnectionManager onStart() {
        if(mCount == 0) {
            mConnectionManagerImpl.connect("10.0.2.2");
        }
        mCount++;
        return mConnectionManagerImpl;
    }

    public static IConnectionManager onStop() {
        mCount--;
        if(mCount == 0 && mConnectionManagerImpl.isConnected()) {
            mConnectionManagerImpl.disconnect();
        }
        return mConnectionManagerImpl;
    }

    public static void restart() {
        if(mCount > 0) {
            mConnectionManagerImpl.disconnect();
            mConnectionManagerImpl.connect("10.0.2.2");
        }
    }
}
