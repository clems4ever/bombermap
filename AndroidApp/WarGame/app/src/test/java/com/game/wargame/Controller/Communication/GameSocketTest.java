package com.game.wargame.Controller.Communication;

import com.game.wargame.Controller.Communication.Game.GameSocket;
import com.game.wargame.Controller.Communication.Game.RemotePlayerSocket;
import com.game.wargame.Controller.Communication.Game.RemotePlayersSocket;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GameSocketTest {

    @Mock
    private ISocket mMockSocket;

    @Mock
    private ISocketFactory mMockSocketFactory;

    @Mock
    private GameSocket.OnPlayerEventListener mMockOnPlayerEventListener;

    @Test
    public void testThatGameSocketCorrectlyRegisterPlayerJoinEvent() {

        GameSocket gameSocket = new GameSocket("game_id", mMockSocket, mMockSocketFactory);

        verify(mMockSocket).on(eq("player_join"), Matchers.<ISocket.OnRemoteEventReceivedListener>any());
    }

    @Test
    public void testThatGameSocketCorrectlyRegisterPlayerLeaveEvent() {

        GameSocket gameSocket = new GameSocket("game_id", mMockSocket, mMockSocketFactory);

        verify(mMockSocket).on(eq("player_leave"), Matchers.<ISocket.OnRemoteEventReceivedListener>any());
    }

    @Test
    public void testThatOnPlayerEventListenerIsCalledWhenAnJoinEventIsSent() throws JSONException {
        RemotePlayersSocket remotePlayersSocket = new RemotePlayersSocket(mMockSocket);
        RemotePlayerSocket remotePlayerSocket = new RemotePlayerSocket("player_id", remotePlayersSocket);

        ArgumentCaptor<ISocket.OnRemoteEventReceivedListener> captor = ArgumentCaptor.forClass(ISocket.OnRemoteEventReceivedListener.class);
        when(mMockSocketFactory.buildRemotePlayerSocket(anyString(), anyString())).thenReturn(remotePlayerSocket);

        GameSocket gameSocket = new GameSocket("game_id", mMockSocket, mMockSocketFactory);

        gameSocket.setOnPlayerEventListener(mMockOnPlayerEventListener);

        verify(mMockSocket).on(eq("player_join"), captor.capture());

        ISocket.OnRemoteEventReceivedListener listener = captor.getValue();

        JSONObject message = new JSONObject();
        message.put("player_id", "player_id");

        listener.onRemoteEventReceived(message);

        verify(mMockOnPlayerEventListener).onPlayerJoined(refEq(remotePlayerSocket));
    }

    @Test
    public void testThatOnPlayerEventListenerIsCalledWhenALeaveEventIsSent() throws JSONException {
        RemotePlayersSocket remotePlayersSocket = new RemotePlayersSocket(mMockSocket);
        RemotePlayerSocket remotePlayerSocket = new RemotePlayerSocket("player_id", remotePlayersSocket);

        ArgumentCaptor<ISocket.OnRemoteEventReceivedListener> captor = ArgumentCaptor.forClass(ISocket.OnRemoteEventReceivedListener.class);
        when(mMockSocketFactory.buildRemotePlayerSocket(anyString(), anyString())).thenReturn(remotePlayerSocket);

        GameSocket gameSocket = new GameSocket("game_id", mMockSocket, mMockSocketFactory);

        gameSocket.setOnPlayerEventListener(mMockOnPlayerEventListener);

        verify(mMockSocket).on(eq("player_leave"), captor.capture());

        ISocket.OnRemoteEventReceivedListener listener = captor.getValue();

        JSONObject message = new JSONObject();
        message.put("player_id", "player_id");

        listener.onRemoteEventReceived(message);

        verify(mMockOnPlayerEventListener).onPlayerLeft(refEq(remotePlayerSocket));
    }

    @Test
    public void testThatGameSocketStoreTheGameId() {

        GameSocket gameSocket = new GameSocket("game_id", mMockSocket, mMockSocketFactory);

        assertEquals("game_id", gameSocket.getGameId());

    }
}
