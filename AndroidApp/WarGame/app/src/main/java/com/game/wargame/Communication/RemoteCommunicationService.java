package com.game.wargame.Communication;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.game.wargame.Communication.RabbitMQ.RabbitMQSocket;

/**
 * Created by clement on 11/02/16.
 */
public class RemoteCommunicationService extends Service {

    private final RemoteCommunicationServiceBinder mRemoteCommunicationServiceBinder = new RemoteCommunicationServiceBinder();

    private GameEngineSocket mGameEngineSocket;

    public RemoteCommunicationService() {

        RabbitMQSocket rabbitMqSocket = new RabbitMQSocket("10.0.2.2");
        mGameEngineSocket = new GameEngineSocket(rabbitMqSocket);
    }

    public class RemoteCommunicationServiceBinder extends Binder {
        public RemoteCommunicationService getService() {
            return RemoteCommunicationService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mRemoteCommunicationServiceBinder;
    }

    public GameEngineSocket getGameEngineSocket() {
        return mGameEngineSocket;
    }
}
