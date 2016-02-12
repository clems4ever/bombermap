package com.game.wargame.Communication;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by clement on 11/02/16.
 */
public class RemoteCommunicationService extends Service {

    private final RemoteCommunicationServiceBinder mRemoteCommunicationServiceBinder = new RemoteCommunicationServiceBinder();

    private RemoteCommunicationSystem mRemoteCommunicationSystem = new RemoteCommunicationSystem();

    public class RemoteCommunicationServiceBinder extends Binder {
        public RemoteCommunicationService getService() {
            return RemoteCommunicationService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mRemoteCommunicationServiceBinder;
    }

    public RemoteCommunicationSystem getRemoteCommunicationSystem() {
        return mRemoteCommunicationSystem;
    }
}
