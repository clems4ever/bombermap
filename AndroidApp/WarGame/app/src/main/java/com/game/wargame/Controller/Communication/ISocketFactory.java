package com.game.wargame.Controller.Communication;

import com.game.wargame.Controller.Communication.Game.GameManagerSocket;
import com.game.wargame.Controller.Communication.Game.GameSocket;
import com.game.wargame.Controller.Communication.Game.LocalPlayerSocket;
import com.game.wargame.Controller.Communication.Game.RegistryManagerSocket;
import com.game.wargame.Controller.Communication.Game.RemotePlayerSocket;

public interface ISocketFactory {

    public GameManagerSocket buildGameManagerSocket();
    public RegistryManagerSocket buildRegistryManagerSocket();
    public GameSocket buildGameSocket(String gameId);
    public RemotePlayerSocket buildRemotePlayerSocket(String gameId, String playerId);
    public LocalPlayerSocket buildLocalPlayerSocket(String gameId, String playerId);
    public ISocket buildDirectPeerSocket(String gameId, RemotePlayerSocket remotePlayerSocket);

}
