package com.game.wargame.Communication;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

/**
 * Created by clement on 12/02/16.
 */
public class RemoteCommunicationServiceConnection implements ServiceConnection {

    RemoteCommunicationService mService;
    boolean mBound = false;

    String mRoom;
    OnServiceConnectedListener mOnServiceConnectedListener;

    public RemoteCommunicationServiceConnection(OnServiceConnectedListener onServiceConnectedListener) {
        mOnServiceConnectedListener = onServiceConnectedListener;
    }

    @Override
    public void onServiceConnected(ComponentName className, IBinder service) {
        RemoteCommunicationService.RemoteCommunicationServiceBinder binder = (RemoteCommunicationService.RemoteCommunicationServiceBinder) service;
        mService = binder.getService();
        mBound = true;

        if(mOnServiceConnectedListener != null) {
            mOnServiceConnectedListener.onServiceConnectedListener();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName arg0) {
        mService = null;
        mBound = false;
    }

    public RemoteCommunicationService getService() {
        return mService;
    }

    public boolean getBound() {
        return mBound;
    }

    public void unbind() {
        mBound = false;
    }

    public interface OnServiceConnectedListener {
        public void onServiceConnectedListener();
    }
}
