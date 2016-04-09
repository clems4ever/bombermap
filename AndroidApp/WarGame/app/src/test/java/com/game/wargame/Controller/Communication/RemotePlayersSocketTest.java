package com.game.wargame.Controller.Communication;

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

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class RemotePlayersSocketTest {

    @Mock
    private ISocket mMockSocket;

    @Mock
    private RemotePlayerSocket.OnMoveEventListener mMockOnMoveEventListener;
    @Mock
    private RemotePlayerSocket.OnFireEventListener mMockOnFireEventListener;

    @Mock
    private RemotePlayersSocket mMockRemotePlayersSocket;


    @Test
    public void testThatPlayerSocketSubscribeToMoveRemoteMessageWhenAssigningMoveEventListener() {

        RemotePlayersSocket remotePlayersSocket = new RemotePlayersSocket(mMockSocket);
        RemotePlayerSocket playerSocket = new RemotePlayerSocket("player_id", remotePlayersSocket);

        playerSocket.setOnMoveEventListener(mMockOnMoveEventListener);
        verify(mMockSocket).on(Matchers.eq("move"), Matchers.<ISocket.OnRemoteEventReceivedListener>any());
    }

    @Test
    public void testThatPlayerSocketSubscribeToFireRemoteMessageWhenAssigningFireEventListener() {

        RemotePlayersSocket remotePlayersSocket = new RemotePlayersSocket(mMockSocket);
        RemotePlayerSocket playerSocket = new RemotePlayerSocket("player_id", remotePlayersSocket);

        playerSocket.setOnFireEventListener(mMockOnFireEventListener);
        verify(mMockSocket).on(Matchers.eq("fire"), Matchers.<ISocket.OnRemoteEventReceivedListener>any());
    }

    @Test
    public void testRemotePlayerSocketCallsListenerWhenMoveEventReceivedFromSocket() {

        ArgumentCaptor<ISocket.OnRemoteEventReceivedListener> listener = ArgumentCaptor.forClass(ISocket.OnRemoteEventReceivedListener.class);

        RemotePlayersSocket remotePlayersSocket = new RemotePlayersSocket(mMockSocket);
        RemotePlayerSocket remotePlayerSocket = new RemotePlayerSocket("player_id", remotePlayersSocket);

        remotePlayerSocket.setOnMoveEventListener(mMockOnMoveEventListener);

        verify(mMockSocket).on(Matchers.matches("move"), listener.capture());

        JSONObject message = new JSONObject();
        try {
            message.put("channel", "move");
            message.put("player_id", "player_id");
            message.put("lat", 5.0f);
            message.put("long", 10.0f);

            listener.getValue().onRemoteEventReceived(message);

            verify(mMockOnMoveEventListener).onMoveEvent(5.0f, 10.0f);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testRemotePlayerSocketCallsListenerWhenFireEventReceivedFromSocket() {

        ArgumentCaptor<ISocket.OnRemoteEventReceivedListener> listener = ArgumentCaptor.forClass(ISocket.OnRemoteEventReceivedListener.class);

        RemotePlayersSocket remotePlayersSocket = new RemotePlayersSocket(mMockSocket);
        RemotePlayerSocket remotePlayerSocket = new RemotePlayerSocket("player_id", remotePlayersSocket);

        remotePlayerSocket.setOnFireEventListener(mMockOnFireEventListener);

        verify(mMockSocket).on(Matchers.matches("fire"), listener.capture());

        JSONObject message = new JSONObject();
        try {
            message.put("channel", "fire");
            message.put("player_id", "player_id");
            message.put("lat", 5.0f);
            message.put("long", 10.0f);
            message.put("time", 100);

            listener.getValue().onRemoteEventReceived(message);

            verify(mMockOnFireEventListener).onFireEvent(5.0f, 10.0f, 100);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testThatMoveEventIsNotCalledAnymoreAfterThePlayerHasBeenRemoved() {
        ArgumentCaptor<ISocket.OnRemoteEventReceivedListener> listener = ArgumentCaptor.forClass(ISocket.OnRemoteEventReceivedListener.class);

        RemotePlayersSocket remotePlayersSocket = new RemotePlayersSocket(mMockSocket);
        RemotePlayerSocket remotePlayerSocket = new RemotePlayerSocket("player_id", remotePlayersSocket);

        remotePlayerSocket.setOnFireEventListener(mMockOnFireEventListener);

        verify(mMockSocket).on(Matchers.matches("fire"), listener.capture());

        JSONObject message = new JSONObject();
        try {
            message.put("channel", "fire");
            message.put("player_id", "player_id");
            message.put("lat", 5.0f);
            message.put("long", 10.0f);
            message.put("time", 100);

            remotePlayersSocket.removePlayer(remotePlayerSocket);

            listener.getValue().onRemoteEventReceived(message);

            verify(mMockOnFireEventListener, times(0)).onFireEvent(5.0f, 10.0f, 100);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
