package com.game.wargame.Controller;

import android.content.Context;

import com.game.wargame.Controller.Communication.Game.GameSocket;
import com.game.wargame.Controller.Communication.Game.LocalPlayerSocket;
import com.game.wargame.Controller.Communication.Game.PlayerSocket;
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
    private GameSocket mMockGameEngineSocket;

    @Mock
    private LocationRetriever mMockLocationRetriever;

    @Mock
    private LocalPlayerSocket mMockLocalPlayerSocket;

    private GameEngine mGameEngine;

    @Before
    public void setUp() {
        mGameEngine = new GameEngine();
    }

    @Test
    public void testIfLocalPlayersIsCorrectlyAddedAtTheBeginningOfTheGame() {
        mGameEngine.onStart(mMockGameView, mMockGameEngineSocket, mMockLocalPlayerSocket, mMockLocationRetriever);

        assertEquals(1, mGameEngine.getPlayersCount());
    }
}
