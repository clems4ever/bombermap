package com.game.wargame.Controller.Communication;


public abstract class Socket implements IEventSocket {

    protected IConnectionManager mConnectionManager;

    public Socket(IConnectionManager connectionManager) {
        mConnectionManager = connectionManager;
    }

    public IConnectionManager getConnectionManager() {
        return mConnectionManager;
    }
}
