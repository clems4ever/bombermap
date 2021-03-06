package com.game.wargame.Controller.Communication;

import com.game.wargame.Controller.Communication.Game.GameManagerSocket;
import com.game.wargame.Controller.Communication.Game.GameSocket;
import com.game.wargame.Controller.Communication.Game.LocalPlayerSocket;
import com.game.wargame.Controller.Communication.Game.RemotePlayerSocket;

public interface IConnectionManager {

    public void connect(String host);
    public void disconnect();

    public void setOnDisconnected(OnDisconnectedListener onDisconnectedListener);

    public void initSocketFactory();

    public boolean isConnected();

    public void freeze();
    public void unfreeze();

    public ISocketFactory getSocketFactory();

    public void clear();

    public interface OnDisconnectedListener {
        public void onDisconnected();
    }
}
