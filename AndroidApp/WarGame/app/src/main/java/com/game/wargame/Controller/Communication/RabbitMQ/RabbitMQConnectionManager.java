package com.game.wargame.Controller.Communication.RabbitMQ;

import com.game.wargame.Controller.Communication.Game.GameManagerSocket;
import com.game.wargame.Controller.Communication.Game.GameSocket;
import com.game.wargame.Controller.Communication.IConnectionManager;
import com.game.wargame.Controller.Communication.IEventSocket;
import com.game.wargame.Controller.Communication.Game.LocalPlayerSocket;
import com.game.wargame.Controller.Communication.Game.RemotePlayerSocket;
import com.game.wargame.Controller.Communication.Game.RemotePlayerSocketRouter;
import com.game.wargame.Controller.Communication.Socket;

public class RabbitMQConnectionManager implements IConnectionManager {

    private RabbitMQConnectionThread mConnectionThread;
    private RemotePlayerSocketRouter mRemotePlayerSocketRouter;

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

    public RabbitMQConnectionThread getConnectionThread() {
        return mConnectionThread;
    }

    public GameManagerSocket buildGameManagerSocket() {
        return new GameManagerSocket(new RabbitMQSocket(this, "", ""));
    }

    @Override
    public GameSocket buildGameSocket(String gameId) {
        return new GameSocket(gameId, new RabbitMQSocket(this, gameId + "_game_room", ""));
    }

    @Override
    public RemotePlayerSocket buildRemotePlayerSocket(String gameId, String playerId) {

        if(mRemotePlayerSocketRouter == null) {
            IEventSocket socket = new RabbitMQSocket(this, gameId + "_game_room", "");
            mRemotePlayerSocketRouter = new RemotePlayerSocketRouter(socket);
        }

        RemotePlayerSocket playerSocket = new RemotePlayerSocket(playerId);
        mRemotePlayerSocketRouter.addPlayer(playerSocket);
        return playerSocket;
    }

    @Override
    public LocalPlayerSocket buildLocalPlayerSocket(String gameId, String playerId) {
        return new LocalPlayerSocket(playerId, new RabbitMQSocket(this, gameId + "_game_room", "all_but_" + playerId));
    }

    @Override
    public Socket buildDirectPeerSocket(String gameId, RemotePlayerSocket remotePlayerSocket) {
        return new RabbitMQSocket(this, gameId + "_game_room", "only_" + remotePlayerSocket.getPlayerId());
    }
}
