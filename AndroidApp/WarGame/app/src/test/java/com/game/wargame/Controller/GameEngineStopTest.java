package com.game.wargame.Controller;

import android.content.Context;

import com.game.wargame.Controller.Communication.Game.GameSocket;
import com.game.wargame.Controller.Communication.Game.LocalPlayerSocket;
import com.game.wargame.Controller.Communication.Game.PlayerSocket;
import com.game.wargame.Controller.Communication.ISocket;
import com.game.wargame.Controller.Communication.ISocketFactory;
import com.game.wargame.Model.Entities.LocalPlayerModel;
import com.game.wargame.Controller.GameEngine;
import com.game.wargame.Views.GameView;
import com.game.wargame.Controller.Sensors.LocationRetriever;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GameEngineStopTest {

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


    @Test
    public void testIfLocationRetrieverIsStoppedWhenGameEngineIsStopped() {

        mGameEngine = new GameEngine();
        mGameEngine.onStart(mMockGameView, mMockGameSocket, mMockLocalPlayerSocket, mMockLocationRetriever);

        mGameEngine.onStop();

        verify(mMockLocationRetriever).stop();
    }
}
