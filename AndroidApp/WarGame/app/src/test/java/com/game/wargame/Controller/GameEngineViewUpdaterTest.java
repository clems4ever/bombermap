package com.game.wargame.Controller;

import android.content.Context;

import com.game.wargame.Controller.Communication.Game.GameSocket;
import com.game.wargame.Controller.Communication.Game.LocalPlayerSocket;
import com.game.wargame.Controller.Communication.Game.RemotePlayerSocket;
import com.game.wargame.Controller.Communication.ISocketFactory;
import com.game.wargame.Controller.Engine.GlobalTimer;
import com.game.wargame.Controller.Sensors.LocationRetriever;
import com.game.wargame.Model.Entities.Players.RemotePlayerModel;
import com.game.wargame.Views.GameView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class GameEngineViewUpdaterTest {

    @Mock
    private GameView mMockGameView;

    @Mock
    private Context mMockContext;

    @Mock
    private GameSocket mMockGameSocket;

    @Mock
    private LocationRetriever mMockLocationRetriever;

    @Mock
    private LocalPlayerSocket mMockPlayerSocket;

    @Mock
    private RemotePlayerSocket mMockRemotePlayerSocket;

    @Mock
    private ISocketFactory mMockSocketFactory;

    @Mock
    private GlobalTimer mMockGlobalTimer;

    private GameEngine mGameEngine;

    @Before
    public void setUp() {
        mGameEngine = new GameEngine();
        mGameEngine.onStart(mMockGameView, mMockGameSocket, mMockPlayerSocket, mMockLocationRetriever, mMockGlobalTimer);
    }

    @Test
    public void testThatPositionOfMovingCurrentPlayerIsUpdated() {
        RemotePlayerModel player = new RemotePlayerModel("player_name", mMockRemotePlayerSocket);
        mGameEngine.onPlayerPositionChanged(player);
        verify(mMockGameView).movePlayer(player);
    }

    @Test
    public void testThatViewIsInitializedWhenGameEngineStarts() {

        verify(mMockGameView).initialize(Matchers.<GameView.OnWeaponTargetDefinedListener>any());

    }
}
