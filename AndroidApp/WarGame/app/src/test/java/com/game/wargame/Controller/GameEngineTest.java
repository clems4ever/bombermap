package com.game.wargame.Controller;

import android.content.Context;

import com.game.wargame.Controller.Communication.Game.GameSocket;
import com.game.wargame.Controller.Communication.Game.LocalPlayerSocket;
import com.game.wargame.Controller.Communication.Game.RemotePlayerSocket;
import com.game.wargame.Controller.Communication.Game.RemotePlayersSocket;
import com.game.wargame.Controller.Communication.ISocket;
import com.game.wargame.Controller.Communication.ISocketFactory;
import com.game.wargame.Controller.Engine.GlobalTimer;
import com.game.wargame.Controller.Sensors.LocationRetriever;
import com.game.wargame.Controller.Settings.Settings;
import com.game.wargame.Model.Entities.Players.LocalPlayerModel;
import com.game.wargame.Model.Entities.Players.RemotePlayerModel;
import com.game.wargame.Model.Entities.VirtualMap.Map;
import com.game.wargame.Model.Entities.VirtualMap.RealMap;
import com.game.wargame.Views.GameView;
import com.game.wargame.Views.MapView;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by clement on 03/03/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class GameEngineTest {

    @Mock private GameView mMockGameView;
    @Mock private MapView mMockMapView;
    @Mock private Context mMockContext;
    @Mock private GameSocket mMockGameSocket;
    @Mock private LocationRetriever mMockLocationRetriever;
    @Mock private Settings mMockSettings;

    @Mock private LocalPlayerSocket mMockLocalPlayerSocket;
    @Mock private RemotePlayerSocket mMockRemotePlayerSocket1;
    @Mock private RemotePlayerSocket mMockRemotePlayerSocket2;
    @Mock private ISocket mMockSocket;
    @Mock private ISocketFactory mMockSocketFactory;
    @Mock private RealMap mMockMap;
    @Mock private GlobalTimer mMockGlobalTimer;

    private GameEngine mGameEngine;

    @Test
    public void testIfLocalPlayersIsCorrectlyAddedAtTheBeginningOfTheGame() {
        when(mMockGameView.getMapView()).thenReturn(mMockMapView);

        mGameEngine = new GameEngine();
        mGameEngine.onStart(mMockGameView, mMockGameSocket, mMockMap, mMockLocalPlayerSocket, mMockLocationRetriever, mMockGlobalTimer, mMockSettings);
        assertEquals(1, mGameEngine.getPlayersCount());
    }

    @Test
    public void testIfLocationRetrieverIsStoppedWhenGameEngineIsStopped() {
        when(mMockGameView.getMapView()).thenReturn(mMockMapView);

        mGameEngine = new GameEngine();
        mGameEngine.onStart(mMockGameView, mMockGameSocket, mMockMap, mMockLocalPlayerSocket, mMockLocationRetriever, mMockGlobalTimer, mMockSettings);

        mGameEngine.onStop();

        verify(mMockLocationRetriever).stop();
    }

    @Test
    public void testIfPlayersAreCorrectlyAddedWhenJoiningTheGame() {

        when(mMockGameView.getMapView()).thenReturn(mMockMapView);

        mGameEngine = new GameEngine();
        mGameEngine.onStart(mMockGameView, mMockGameSocket, mMockMap, mMockLocalPlayerSocket, mMockLocationRetriever, mMockGlobalTimer, mMockSettings);

        RemotePlayersSocket remotePlayersSocket = new RemotePlayersSocket(mMockSocket);
        RemotePlayerSocket playerSocket1 = new RemotePlayerSocket("player_id", remotePlayersSocket);
        mGameEngine.onPlayerJoined(playerSocket1);

        assertEquals(2, mGameEngine.getPlayersCount());

        RemotePlayerSocket playerSocket2 = new RemotePlayerSocket("player_2", remotePlayersSocket);
        mGameEngine.onPlayerJoined(playerSocket2);

        assertEquals(3, mGameEngine.getPlayersCount());
    }

    @Test
    public void testRemotePlayerJoiningIsAddedToTheGame() {
        mGameEngine = new GameEngine();

        when(mMockLocalPlayerSocket.getPlayerId()).thenReturn("local_player");
        when(mMockRemotePlayerSocket1.getPlayerId()).thenReturn("player1");
        when(mMockRemotePlayerSocket1.getPlayerId()).thenReturn("player2");
        when(mMockGameView.getMapView()).thenReturn(mMockMapView);

        mGameEngine.onStart(mMockGameView, mMockGameSocket, mMockMap, mMockLocalPlayerSocket, mMockLocationRetriever, mMockGlobalTimer, mMockSettings);

        mGameEngine.onPlayerJoined(mMockRemotePlayerSocket1);
        mGameEngine.onPlayerJoined(mMockRemotePlayerSocket2);

        assertEquals(3, mGameEngine.getPlayersCount());
    }

    @Test
    public void testRemotePlayerLeavingIsRemoveFromTheGame() {
        mGameEngine = new GameEngine();

        when(mMockLocalPlayerSocket.getPlayerId()).thenReturn("local_player");
        when(mMockRemotePlayerSocket1.getPlayerId()).thenReturn("player1");
        when(mMockRemotePlayerSocket2.getPlayerId()).thenReturn("player2");
        when(mMockGameView.getMapView()).thenReturn(mMockMapView);

        mGameEngine.onStart(mMockGameView, mMockGameSocket, mMockMap, mMockLocalPlayerSocket, mMockLocationRetriever, mMockGlobalTimer, mMockSettings);

        mGameEngine.onPlayerJoined(mMockRemotePlayerSocket1);
        mGameEngine.onPlayerJoined(mMockRemotePlayerSocket2);

        assertEquals(3, mGameEngine.getPlayersCount());

        mGameEngine.onPlayerLeft(mMockRemotePlayerSocket1);

        assertEquals(2, mGameEngine.getPlayersCount());
    }

    @Test
    public void testAddingPlayers() {

        mGameEngine = new GameEngine();

        when(mMockLocalPlayerSocket.getPlayerId()).thenReturn("local_player");
        when(mMockRemotePlayerSocket1.getPlayerId()).thenReturn("player1");
        when(mMockRemotePlayerSocket2.getPlayerId()).thenReturn("player2");
        when(mMockGameView.getMapView()).thenReturn(mMockMapView);

        mGameEngine.onStart(mMockGameView, mMockGameSocket, mMockMap, mMockLocalPlayerSocket, mMockLocationRetriever, mMockGlobalTimer, mMockSettings);

        mGameEngine.onPlayerJoined(mMockRemotePlayerSocket1);
        mGameEngine.onPlayerJoined(mMockRemotePlayerSocket2);

        LocalPlayerModel localPlayerModel = new LocalPlayerModel("username", mMockLocalPlayerSocket);

        RemotePlayerModel remotePlayerModel1 = new RemotePlayerModel("username", mMockRemotePlayerSocket1);
        RemotePlayerModel remotePlayerModel2 = new RemotePlayerModel("username", mMockRemotePlayerSocket2);

        verify(mMockGameView).addRemotePlayer(eq(remotePlayerModel1));
        verify(mMockGameView).addRemotePlayer(eq(remotePlayerModel2));
        verify(mMockGameView).addLocalPlayer(eq(localPlayerModel));

    }

}
