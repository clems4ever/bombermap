package com.game.wargame;

import android.content.Context;

import com.game.wargame.Controller.Communication.Communication.Communication.GameEngineSocket;
import com.game.wargame.Controller.Communication.Communication.Communication.IEventSocket;
import com.game.wargame.Controller.Communication.Communication.Communication.PlayerSocket;
import com.game.wargame.Model.Entities.LocalPlayerModel;
import com.game.wargame.Controller.Communication.Communication.Communication.GameEngine;
import com.game.wargame.Views.GameView;
import com.game.wargame.Controller.Sensors.LocationRetriever;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GameEnginePlayerJoinedTest {

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

    @Mock
    private IEventSocket mMockEventSocket;

    private GameEngine mGameEngine;

    @Before
    public void setUp() {
        mGameEngine = new GameEngine(mMockContext, mMockGameEngineSocket, mMockLocationRetriever);

        when(mMockGameEngineSocket.getLocalPlayerSocket()).thenReturn(mMockPlayerSocket);

        PlayerSocket playerSocket = mMockGameEngineSocket.getLocalPlayerSocket();

        LocalPlayerModel setupLocalPlayer = new LocalPlayerModel("Clement", playerSocket);
        mGameEngine.start(mMockGameView, setupLocalPlayer);
    }


    @Test
    public void testIfPlayersAreCorrectlyAddedWhenJoiningTheGame() {

        PlayerSocket playerSocket1 = new PlayerSocket("player_1", mMockEventSocket);
        mGameEngine.onPlayerJoined(playerSocket1);

        assertEquals(2, mGameEngine.getPlayersCount());

        PlayerSocket playerSocket2 = new PlayerSocket("player_2", mMockEventSocket);
        mGameEngine.onPlayerJoined(playerSocket2);

        assertEquals(3, mGameEngine.getPlayersCount());
    }
}
