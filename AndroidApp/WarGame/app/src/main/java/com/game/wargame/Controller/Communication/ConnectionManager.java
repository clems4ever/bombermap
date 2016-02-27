package com.game.wargame.Controller.Communication;

import com.game.wargame.Controller.Communication.RabbitMQ.RabbitMQConnectionManager;

public class ConnectionManager {

    private static int mCount = 0;
    private static IConnectionManager mConnectionManagerImpl;

    private ConnectionManager() {
        mConnectionManagerImpl = new RabbitMQConnectionManager("10.0.2.2");
    }

    public static IConnectionManager getInstance() {
        return mConnectionManagerImpl;
    }

    public static IConnectionManager onStart() {
        if(mCount == 0) {
            mConnectionManagerImpl.connect();
        }
        mCount++;

        return mConnectionManagerImpl;
    }

    public static void onStop() {
        mCount--;
        if(mCount == 0) {
            mConnectionManagerImpl.disconnect();
        }
    }
}
