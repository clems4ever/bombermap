package com.game.wargame.Controller.Communication;

import com.game.wargame.Controller.Communication.Game.GameManagerSocket;
import com.game.wargame.Controller.Communication.Game.GameSocket;
import com.game.wargame.Controller.Communication.Game.LocalPlayerSocket;
import com.game.wargame.Controller.Communication.Game.RemotePlayerSocket;

public interface IConnectionManager {

    public void connect();
    public void disconnect();

    public void setOnDisconnected(OnDisconnectedListener onDisconnectedListener);

    public boolean isConnected();

    public GameManagerSocket buildGameManagerSocket();
    public GameSocket buildGameSocket(String gameId);
    public RemotePlayerSocket buildRemotePlayerSocket(String gameId, String playerId);
    public LocalPlayerSocket buildLocalPlayerSocket(String gameId, String playerId);
    public Socket buildDirectPeerSocket(String gameId, RemotePlayerSocket remotePlayerSocket);

    public interface OnDisconnectedListener {
        public void onDisconnected();
    }
}
