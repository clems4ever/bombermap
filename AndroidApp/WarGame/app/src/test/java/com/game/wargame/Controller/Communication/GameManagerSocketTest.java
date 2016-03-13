package com.game.wargame.Controller.Communication;

import com.game.wargame.Controller.Communication.Game.GameManagerSocket;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class GameManagerSocketTest {

    @Mock
    private ISocket mMockSocket;

    @Mock
    private GameManagerSocket.OnGameCreatedListener mMockOnGameCreatedListener;

    @Mock
    private GameManagerSocket.OnGameJoinedListener mMockOnGameJoinedListener;

    @Mock
    private ISocket.OnRemoteEventReceivedListener mMockOnRemoteEventReceivedListener;


    @Test
    public void testCreateGameMustCallCreateGameListener() throws JSONException {

        ArgumentCaptor<ISocket.OnRemoteEventReceivedListener> captor = ArgumentCaptor.forClass(ISocket.OnRemoteEventReceivedListener.class);
        GameManagerSocket gameManagerSocket = new GameManagerSocket(mMockSocket);

        gameManagerSocket.createGame(mMockOnGameCreatedListener);

        JSONObject message = new JSONObject();
        message.put("action", "newgame");

        verify(mMockSocket).call(eq("global_queue"), refEq(message), captor.capture());

        ISocket.OnRemoteEventReceivedListener listener = captor.getValue();

        JSONObject reply = new JSONObject();
        reply.put("game_id", "abc");
        listener.onRemoteEventReceived(reply);

        verify(mMockOnGameCreatedListener).onGameCreated(eq("abc"));
    }

    @Test
    public void testJoinGameMustCallJoinGameListener() throws JSONException {
        ArgumentCaptor<ISocket.OnRemoteEventReceivedListener> captor = ArgumentCaptor.forClass(ISocket.OnRemoteEventReceivedListener.class);
        GameManagerSocket gameManagerSocket = new GameManagerSocket(mMockSocket);

        gameManagerSocket.joinGame("abc", mMockOnGameJoinedListener);

        JSONObject message = new JSONObject();
        message.put("action", "join");
        message.put("game_id", "abc");

        verify(mMockSocket).call(eq("global_queue"), refEq(message), captor.capture());

        ISocket.OnRemoteEventReceivedListener listener = captor.getValue();

        JSONObject reply = new JSONObject();
        reply.put("player_id", "player_abc");
        listener.onRemoteEventReceived(reply);

        verify(mMockOnGameJoinedListener).onGameJoined(eq("player_abc"));
    }

    @Test
    public void testLeaveGameMustCallLeaveGameListener() throws JSONException {
        ArgumentCaptor<ISocket.OnRemoteEventReceivedListener> captor = ArgumentCaptor.forClass(ISocket.OnRemoteEventReceivedListener.class);
        GameManagerSocket gameManagerSocket = new GameManagerSocket(mMockSocket);

        gameManagerSocket.leaveGame("game_abc", "player_abc");

        JSONObject message = new JSONObject();
        message.put("action", "leave");
        message.put("game_id", "game_abc");
        message.put("player_id", "player_abc");

        verify(mMockSocket).call(eq("global_queue"), refEq(message), captor.capture());

        ISocket.OnRemoteEventReceivedListener listener = captor.getValue();

        JSONObject reply = new JSONObject();
        listener.onRemoteEventReceived(reply);
    }

    @Test(expected = JSONException.class)
    public void testThatMalformedReplyMustNotCallListener() throws JSONException {

        ArgumentCaptor<ISocket.OnRemoteEventReceivedListener> captor = ArgumentCaptor.forClass(ISocket.OnRemoteEventReceivedListener.class);
        GameManagerSocket gameManagerSocket = new GameManagerSocket(mMockSocket);

        gameManagerSocket.createGame(mMockOnGameCreatedListener);

        JSONObject message = new JSONObject();
        message.put("action", "newgame");

        verify(mMockSocket).call(eq("global_queue"), refEq(message), captor.capture());

        ISocket.OnRemoteEventReceivedListener listener = captor.getValue();

        JSONObject reply = new JSONObject();
        reply.put("game_idaa", "abc");
        listener.onRemoteEventReceived(reply);

        verify(mMockOnGameCreatedListener, times(0)).onGameCreated(eq("abc"));
    }

}
