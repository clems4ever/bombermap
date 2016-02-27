package com.game.wargame;

import android.content.Context;

import com.game.wargame.Controller.Communication.PlayerSocket;
import com.game.wargame.Model.Entities.LocalPlayerModel;
import com.game.wargame.Controller.GameEngine;
import com.game.wargame.Views.GameView;
import com.game.wargame.Controller.Sensors.LocationRetriever;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class GameEngineSetupTest {

    @Mock
    private GameView mMockGameView;

    @Mock
    private Context mMockContext;

    @Mock
    private GameEngineSocket mMockGameEngineSocket;

    @Mock
    private LocationRetriever mMockLocationRetriever;

    @Mock
    private PlayerSocket mMockPlayerSocket;

    private GameEngine mGameEngine;

    @Before
    public void setUp() {
        mGameEngine = new GameEngine(mMockContext, mMockGameEngineSocket, mMockLocationRetriever);

        when(mMockGameEngineSocket.getLocalPlayerSocket()).thenReturn(mMockPlayerSocket);
    }

    @Test
    public void testIfLocalPlayersIsCorrectlyAddedAtTheBeginningOfTheGame() {
        PlayerSocket playerSocket = mMockGameEngineSocket.getLocalPlayerSocket();

        LocalPlayerModel setupLocalPlayer = new LocalPlayerModel("Clement", playerSocket);
        mGameEngine.start(mMockGameView, setupLocalPlayer);

        LocalPlayerModel localPlayer = mGameEngine.getLocalPlayer();

        assertTrue(setupLocalPlayer == localPlayer);
        assertEquals(1, mGameEngine.getPlayersCount());
    }
}
