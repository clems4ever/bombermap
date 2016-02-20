package com.game.wargame.Communication;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PlayerSocketTest {

    @Mock
    private IEventSocket mMockEventSocket;

    @Mock
    private PlayerSocket.OnMoveEventListener mMockOnMoveEventListener;
    @Mock
    private PlayerSocket.OnFireEventListener mMockOnFireEventListener;

    @Test
    public void testThatPlayerSocketSubscribeToMoveRemoteMessageWhenAssigningMoveEventListener() {

        PlayerSocket playerSocket = new PlayerSocket("player_id", mMockEventSocket);

        playerSocket.setOnMoveEventListener(mMockOnMoveEventListener);

        verify(mMockEventSocket).on(Matchers.eq("move"), Matchers.<IEventSocket.OnRemoteEventReceivedListener>any());
    }

    @Test
    public void testThatPlayerSocketSubscribeToFireRemoteMessageWhenAssigningFireEventListener() {

        PlayerSocket playerSocket = new PlayerSocket("player_id", mMockEventSocket);

        playerSocket.setOnFireEventListener(mMockOnFireEventListener);

        verify(mMockEventSocket).on(Matchers.eq("fire"), Matchers.<IEventSocket.OnRemoteEventReceivedListener>any());
    }
}
