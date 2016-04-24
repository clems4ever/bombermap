package com.game.wargame.Controller.Communication.RabbitMQ;

import android.os.Environment;

import com.game.wargame.AppConstant;
import com.game.wargame.Controller.Communication.IConnectionManager;
import com.game.wargame.Controller.Communication.ISocketFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

public class RabbitMQConnectionManager implements IConnectionManager {

    private RabbitMQConnectionThread mConnectionThread;
    private RabbitMQSocketFactory mSocketFactory;
    private String mVirtualHost;


    public RabbitMQConnectionManager(String virtualHost) {
        mVirtualHost = virtualHost;
    }

    @Override
    public void connect(String host) {
        mConnectionThread = new RabbitMQConnectionThread(host, mVirtualHost, AppConstant.COMMUNICATION_DUMP_ENABLED);
        mConnectionThread.start();
        mSocketFactory = new RabbitMQSocketFactory(mConnectionThread);
    }

    @Override
    public void initSocketFactory() {
        mSocketFactory.initRemotePlayersSocket();
    }

    @Override
    public void disconnect() {
        RabbitMQMessageLogger logger = mConnectionThread.messageLogger();
        if(logger != null) {
            String s = mConnectionThread.messageLogger().dump();
            String logFile = Environment.getExternalStorageDirectory().getPath() + "/comm.dump";
            try {
                PrintWriter fout = new PrintWriter(logFile);
                fout.write(s);
                fout.flush();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
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
        mConnectionThread.freeze();
    }

    @Override
    public void unfreeze() {
        mConnectionThread.unfreeze();
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
