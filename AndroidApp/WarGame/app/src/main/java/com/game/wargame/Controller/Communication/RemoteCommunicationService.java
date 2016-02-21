package com.game.wargame.Controller.Communication;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class RemoteCommunicationService extends Service {

    private final RemoteCommunicationServiceBinder mRemoteCommunicationServiceBinder = new RemoteCommunicationServiceBinder();

    private GameEngineSocket mGameEngineSocket;

    public RemoteCommunicationService() {
    }

    public void initialize(IEventSocket eventSocket) {
        mGameEngineSocket = new GameEngineSocket(eventSocket);
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
