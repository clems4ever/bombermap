package com.game.wargame;

import com.game.wargame.Communication.IRemoteCommunicationSocket;
import com.game.wargame.Communication.RemoteCommunicationSocket;
import com.game.wargame.Communication.RemoteCommunicationSystem;
import com.github.nkzawa.socketio.client.Ack;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import static org.mockito.Mockito.*;

import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class RemoteCommunicationSystemTest {

    @Mock
    private IRemoteCommunicationSocket mRemoteCommunicationSocketMock;

    @Mock
    private RemoteCommunicationSystem.OnGameCreatedListener mOnGameCreatedListenerMock;

    private RemoteCommunicationSystem mRemoteCommunicationSystem;

    @Before
    public void setUp() {
        mRemoteCommunicationSystem = new RemoteCommunicationSystem(mRemoteCommunicationSocketMock);
    }

    @Test
    public void testConnectFromSocketIsCalled() {
        mRemoteCommunicationSystem.connect();
        verify(mRemoteCommunicationSocketMock, times(1)).connect();
    }

    @Test
    public void testDisconnectFromSocketIsCalled() {
        mRemoteCommunicationSystem.disconnect();
        verify(mRemoteCommunicationSocketMock, times(1)).disconnect();
    }

    @Test
    public void testIsConnectedFromSocket() {
        when(mRemoteCommunicationSocketMock.isConnected()).thenReturn(true);

        assertTrue("isConnected must return true", mRemoteCommunicationSystem.isConnected());
    }

    @Test
    public void testIsNotConnectedFromSocket() {
        when(mRemoteCommunicationSocketMock.isConnected()).thenReturn(false);

        assertTrue("isConnected must return false", !mRemoteCommunicationSystem.isConnected());
    }

    @Test
    public void testCreateGameMustRegisterAcknowledgement() {
        String channel = "new_game";

        RemoteCommunicationSystem.OnGameCreatedListener listener = new RemoteCommunicationSystem.OnGameCreatedListener() {
            @Override
            public void onGameCreated(String gameId) {

            }
        };

        mRemoteCommunicationSystem.createGame(listener);
        verify(mRemoteCommunicationSocketMock, times(1)).emit(Matchers.eq(channel), Matchers.<Ack>any());
    }
}
