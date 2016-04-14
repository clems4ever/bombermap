package com.game.wargame.Controller.Communication.RabbitMQ;

import com.game.wargame.Controller.Communication.Game.GameManagerSocket;
import com.game.wargame.Controller.Communication.Game.GameSocket;
import com.game.wargame.Controller.Communication.Game.LocalPlayerSocket;
import com.game.wargame.Controller.Communication.Game.RegistryManagerSocket;
import com.game.wargame.Controller.Communication.Game.RemotePlayerSocket;
import com.game.wargame.Controller.Communication.Game.RemotePlayersSocket;
import com.game.wargame.Controller.Communication.ISocket;
import com.game.wargame.Controller.Communication.ISocketFactory;


public class RabbitMQSocketFactory implements ISocketFactory {

    private RabbitMQConnectionThread mConnectionThread;
    private RemotePlayersSocket mRemotePlayersSocket;

    public RabbitMQSocketFactory(RabbitMQConnectionThread connectionThread) {
        mConnectionThread = connectionThread;

    }

    public void initRemotePlayersSocket() {
        mRemotePlayersSocket = new RemotePlayersSocket(new RabbitMQSocket(mConnectionThread));
    }

    public GameManagerSocket buildGameManagerSocket() {
        return new GameManagerSocket(new RabbitMQSocket(mConnectionThread, "", ""));
    }

    public RegistryManagerSocket buildRegistryManagerSocket() {
        return new RegistryManagerSocket(new RabbitMQSocket(mConnectionThread, "", ""));
    }

    @Override
    public GameSocket buildGameSocket(String gameId) {
        return new GameSocket(gameId, new RabbitMQSocket(mConnectionThread, gameId + "_game_room", ""), this);
    }

    @Override
    public RemotePlayerSocket buildRemotePlayerSocket(String gameId, String playerId) {
        return new RemotePlayerSocket(playerId, mRemotePlayersSocket);
    }

    @Override
    public LocalPlayerSocket buildLocalPlayerSocket(String gameId, String playerId) {
        return new LocalPlayerSocket(playerId, new RabbitMQSocket(mConnectionThread, gameId + "_game_room", "all_but_" + playerId));
    }

    @Override
    public ISocket buildDirectPeerSocket(String gameId, RemotePlayerSocket remotePlayerSocket) {
        return new RabbitMQSocket(mConnectionThread, gameId + "_game_room", "only_" + remotePlayerSocket.getPlayerId());
    }
}
