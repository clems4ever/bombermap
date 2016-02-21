package com.game.wargame;

import com.game.wargame.Controller.Communication.Communication.Communication.IEventSocket;
import com.game.wargame.Controller.Communication.Communication.Communication.GameEngineSocket;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import static org.mockito.Mockito.*;

import org.mockito.runners.MockitoJUnitRunner;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class GameEngineSocketTest {

    @Mock
    private IEventSocket mEventSocket;

    private GameEngineSocket mGameEngineSocket;

    @Before
    public void setUp() {
        mGameEngineSocket = new GameEngineSocket(mEventSocket);
    }

    @Test
    public void testConnectFromSocketIsCalled() {
        mGameEngineSocket.connect("game_room");
        verify(mEventSocket, times(1)).connect("game_room");
    }

    @Test
    public void testDisconnectFromSocketIsCalled() {
        mGameEngineSocket.disconnect();
        verify(mEventSocket, times(1)).disconnect();
    }

    @Test
    public void testIsConnectedFromSocket() {
        when(mEventSocket.isConnected()).thenReturn(true);

        assertTrue("isConnected must return true", mGameEngineSocket.isConnected());
    }

    @Test
    public void testIsNotConnectedFromSocket() {
        when(mEventSocket.isConnected()).thenReturn(false);

        assertTrue("isConnected must return false", !mGameEngineSocket.isConnected());
    }
}
