package com.game.wargame.Controller;

import android.content.Context;

import com.game.wargame.Controller.Communication.Game.GameSocket;
import com.game.wargame.Controller.Communication.Game.LocalPlayerSocket;
import com.game.wargame.Controller.Communication.Game.RemotePlayerSocket;
import com.game.wargame.Controller.Communication.ISocketFactory;
import com.game.wargame.Controller.Engine.GlobalTimer;
import com.game.wargame.Controller.Sensors.LocationRetriever;
import com.game.wargame.Controller.Settings.Settings;
import com.game.wargame.Model.Entities.Players.RemotePlayerModel;
import com.game.wargame.Model.Entities.VirtualMap.Map;
import com.game.wargame.Model.Entities.VirtualMap.RealMap;
import com.game.wargame.Views.GameView;
import com.game.wargame.Views.MapView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.regex.Matcher;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GameEngineViewUpdaterTest {

    @Mock private GameView mMockGameView;
    @Mock private MapView mMockMapView;
    @Mock private Context mMockContext;
    @Mock private GameSocket mMockGameSocket;
    @Mock private LocationRetriever mMockLocationRetriever;
    @Mock private LocalPlayerSocket mMockPlayerSocket;
    @Mock private RemotePlayerSocket mMockRemotePlayerSocket;
    @Mock private ISocketFactory mMockSocketFactory;
    @Mock private GlobalTimer mMockGlobalTimer;
    @Mock private RealMap mMockVirtualMap;
    @Mock private Settings mMockSettings;

    private GameEngine mGameEngine;

    @Before
    public void setUp() {
        when(mMockGameView.getMapView()).thenReturn(mMockMapView);

        mGameEngine = new GameEngine();
        mGameEngine.onStart(mMockGameView, mMockGameSocket, mMockVirtualMap, mMockPlayerSocket, mMockLocationRetriever, mMockGlobalTimer, mMockSettings);
    }

    @Test
    public void position_of_moving_current_player_is_updated() {
        RemotePlayerModel player = new RemotePlayerModel("player_name", mMockRemotePlayerSocket);
        mGameEngine.onRemotePlayerPositionChanged(player);
        verify(mMockGameView).movePlayer(player);
    }

    @Test
    public void testThatViewIsInitializedWhenGameEngineStarts() {

        verify(mMockGameView).initialize(Matchers.<GameView.OnWeaponTargetDefinedListener>any(), Matchers.<GameView.OnShieldListener>any());

    }


}
