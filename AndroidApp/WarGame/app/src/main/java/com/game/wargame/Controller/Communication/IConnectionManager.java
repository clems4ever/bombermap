package com.game.wargame.Controller.Communication;

public interface IConnectionManager {

    public void connect();
    public void disconnect();

    public void setOnDisconnected(OnDisconnectedListener onDisconnectedListener);

    public boolean isConnected();

    public GameManagerSocket buildGameManagerSocket();
    public GameSocket buildGameSocket(String gameId);
    public RemotePlayerSocket buildRemotePlayerSocket(String gameId, String playerId);
    public LocalPlayerSocket buildLocalPlayerSocket(String gameId, String playerId);

    public interface OnDisconnectedListener {
        public void onDisconnected();
    }
}
