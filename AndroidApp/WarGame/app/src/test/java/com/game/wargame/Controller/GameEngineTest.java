package com.game.wargame.Controller;

import android.content.Context;

import com.game.wargame.Controller.Communication.Game.GameSocket;
import com.game.wargame.Controller.Communication.Game.LocalPlayerSocket;
import com.game.wargame.Controller.Communication.Game.RemotePlayerSocket;
import com.game.wargame.Controller.Communication.Game.RemotePlayersSocket;
import com.game.wargame.Controller.Communication.ISocket;
import com.game.wargame.Controller.Communication.ISocketFactory;
import com.game.wargame.Controller.Engine.ProjectilesUpdateTimer;
import com.game.wargame.Controller.Sensors.LocationRetriever;
import com.game.wargame.Model.Entities.LocalPlayerModel;
import com.game.wargame.Model.Entities.PlayerModel;
import com.game.wargame.Model.Entities.RemotePlayerModel;
import com.game.wargame.Views.GameView;
import com.game.wargame.Views.MapView;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by clement on 03/03/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class GameEngineTest {

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
    private RemotePlayerSocket mMockRemotePlayerSocket1;

    @Mock
    private RemotePlayerSocket mMockRemotePlayerSocket2;

    @Mock
    private ISocket mMockSocket;

    @Mock
    private ISocketFactory mMockSocketFactory;

    @Mock
    private ProjectilesUpdateTimer mMockProjectilesUpdateTimer;

    private GameEngine mGameEngine;

    @Test
    public void testIfLocalPlayersIsCorrectlyAddedAtTheBeginningOfTheGame() {
        mGameEngine = new GameEngine();
        mGameEngine.onStart(mMockGameView, mMockGameSocket, mMockLocalPlayerSocket, mMockLocationRetriever, mMockProjectilesUpdateTimer);
        assertEquals(1, mGameEngine.getPlayersCount());
    }

    @Test
    public void testIfLocationRetrieverIsStoppedWhenGameEngineIsStopped() {

        mGameEngine = new GameEngine();
        mGameEngine.onStart(mMockGameView, mMockGameSocket, mMockLocalPlayerSocket, mMockLocationRetriever, mMockProjectilesUpdateTimer);

        mGameEngine.onStop();

        verify(mMockLocationRetriever).stop();
    }

    @Test
    public void testIfPlayersAreCorrectlyAddedWhenJoiningTheGame() {

        mGameEngine = new GameEngine();
        mGameEngine.onStart(mMockGameView, mMockGameSocket, mMockLocalPlayerSocket, mMockLocationRetriever, mMockProjectilesUpdateTimer);

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

        mGameEngine.onStart(mMockGameView, mMockGameSocket, mMockLocalPlayerSocket, mMockLocationRetriever, mMockProjectilesUpdateTimer);

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

        mGameEngine.onStart(mMockGameView, mMockGameSocket, mMockLocalPlayerSocket, mMockLocationRetriever, mMockProjectilesUpdateTimer);

        mGameEngine.onPlayerJoined(mMockRemotePlayerSocket1);
        mGameEngine.onPlayerJoined(mMockRemotePlayerSocket2);

        assertEquals(3, mGameEngine.getPlayersCount());

        mGameEngine.onPlayerLeft(mMockRemotePlayerSocket1);

        assertEquals(2, mGameEngine.getPlayersCount());
    }

    @Test
    public void testAddingPlayersWhenMapIsLoaded() {

        mGameEngine = new GameEngine();

        when(mMockLocalPlayerSocket.getPlayerId()).thenReturn("local_player");
        when(mMockRemotePlayerSocket1.getPlayerId()).thenReturn("player1");
        when(mMockRemotePlayerSocket2.getPlayerId()).thenReturn("player2");

        mGameEngine.onStart(mMockGameView, mMockGameSocket, mMockLocalPlayerSocket, mMockLocationRetriever, mMockProjectilesUpdateTimer);

        mGameEngine.onPlayerJoined(mMockRemotePlayerSocket1);
        mGameEngine.onPlayerJoined(mMockRemotePlayerSocket2);

        ArgumentCaptor<MapView.OnMapReadyListener> onMapReadyListenerArgumentCaptor = ArgumentCaptor.forClass(MapView.OnMapReadyListener.class);

        verify(mMockGameView).loadMap(onMapReadyListenerArgumentCaptor.capture());

        onMapReadyListenerArgumentCaptor.getValue().onMapReady();

        LocalPlayerModel localPlayerModel = new LocalPlayerModel("username", mMockLocalPlayerSocket);

        RemotePlayerModel remotePlayerModel1 = new RemotePlayerModel("username", mMockRemotePlayerSocket1);
        RemotePlayerModel remotePlayerModel2 = new RemotePlayerModel("username", mMockRemotePlayerSocket2);

        verify(mMockGameView).addRemotePlayer(eq(remotePlayerModel1));
        verify(mMockGameView).movePlayer(eq(remotePlayerModel1));

        verify(mMockGameView).addRemotePlayer(eq(remotePlayerModel2));
        verify(mMockGameView).movePlayer(eq(remotePlayerModel2));

        verify(mMockGameView).addLocalPlayer(eq(localPlayerModel));
        verify(mMockGameView).movePlayer(eq(localPlayerModel));

    }

}
