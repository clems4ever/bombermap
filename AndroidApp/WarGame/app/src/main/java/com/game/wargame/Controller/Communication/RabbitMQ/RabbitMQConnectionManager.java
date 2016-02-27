package com.game.wargame.Controller.Communication.RabbitMQ;

import com.game.wargame.Controller.Communication.GameManagerSocket;
import com.game.wargame.Controller.Communication.GameSocket;
import com.game.wargame.Controller.Communication.IConnectionManager;
import com.game.wargame.Controller.Communication.LocalPlayerSocket;
import com.game.wargame.Controller.Communication.RemotePlayerSocket;

public class RabbitMQConnectionManager implements IConnectionManager {

    private RabbitMQConnectionThread mConnectionThread;

    public RabbitMQConnectionManager(String host) {
        mConnectionThread = new RabbitMQConnectionThread(host);
    }

    @Override
    public void connect() {
        mConnectionThread.start();
    }

    @Override
    public void disconnect() {
        mConnectionThread.disconnect();
    }

    @Override
    public void setOnDisconnected(OnDisconnectedListener onDisconnectedListener) {
        mConnectionThread.setOnDisconnectedListener(onDisconnectedListener);
    }

    @Override
    public boolean isConnected() {
        return true;
    }

    public GameManagerSocket buildGameManagerSocket() {
        return new GameManagerSocket(new RabbitMQSocket(mConnectionThread, "", ""), this);
    }

    @Override
    public GameSocket buildGameSocket(String gameId) {
        return new GameSocket(gameId, new RabbitMQSocket(mConnectionThread, gameId + "_game_room", ""), this);
    }

    @Override
    public RemotePlayerSocket buildRemotePlayerSocket(String gameId, String playerId) {
        return new RemotePlayerSocket(playerId, new RabbitMQSocket(mConnectionThread, gameId + "_game_room", "all_but_" + playerId));
    }

    @Override
    public LocalPlayerSocket buildLocalPlayerSocket(String gameId, String playerId) {
        return new LocalPlayerSocket(playerId, new RabbitMQSocket(mConnectionThread, gameId + "_game_room", "all_but_" + playerId));
    }
}
