package com.game.wargame.Controller;

import android.content.Context;

import com.game.wargame.Controller.Communication.Game.GameSocket;
import com.game.wargame.Controller.Communication.Game.LocalPlayerSocket;
import com.game.wargame.Controller.Communication.Game.RemotePlayerSocket;
import com.game.wargame.Controller.Communication.Game.RemotePlayersSocket;
import com.game.wargame.Controller.Communication.ISocket;
import com.game.wargame.Controller.Communication.Game.PlayerSocket;
import com.game.wargame.Controller.Communication.ISocketFactory;
import com.game.wargame.Controller.GameEngine;
import com.game.wargame.Controller.Sensors.LocationRetriever;
import com.game.wargame.Model.Entities.LocalPlayerModel;
import com.game.wargame.Views.GameView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GameEnginePlayerJoinedTest {

    @Mock
    private GameView mMockGameView;

    @Mock
    private Context mMockContext;

    @Mock
    private GameSocket mMockGameSocket;

    @Mock
    private LocationRetriever mMockLocationRetriever;

    @Mock
    private LocalPlayerSocket mMockLocalPlayerSocket;

    @Mock
    private ISocket mMockSocket;

    @Mock
    private ISocketFactory mMockSocketFactory;

    private GameEngine mGameEngine;

    @Before
    public void setUp() {
        mGameEngine = new GameEngine();

        mGameEngine.onStart(mMockGameView, mMockGameSocket, mMockLocalPlayerSocket, mMockLocationRetriever);
    }


    @Test
    public void testIfPlayersAreCorrectlyAddedWhenJoiningTheGame() {

        RemotePlayersSocket remotePlayersSocket = new RemotePlayersSocket(mMockSocket);
        RemotePlayerSocket playerSocket1 = new RemotePlayerSocket("player_id", remotePlayersSocket);
        mGameEngine.onPlayerJoined(playerSocket1);

        assertEquals(2, mGameEngine.getPlayersCount());

        RemotePlayerSocket playerSocket2 = new RemotePlayerSocket("player_2", remotePlayersSocket);
        mGameEngine.onPlayerJoined(playerSocket2);

        assertEquals(3, mGameEngine.getPlayersCount());
    }
}
