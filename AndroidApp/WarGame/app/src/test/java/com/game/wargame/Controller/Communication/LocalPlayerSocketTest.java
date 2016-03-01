package com.game.wargame.Controller.Communication;

import com.game.wargame.Controller.Communication.Game.LocalPlayerSocket;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

/**
 * Created by clement on 02/03/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class LocalPlayerSocketTest {

    @Mock
    private ISocket mMockSocket;

    @Mock
    private ISocketFactory mMockSocketFactory;

    @Test
    public void testThatThePlayerSendAFireMessageViaSocketWhenFireIsCalled() throws JSONException {

        LocalPlayerSocket localPlayerSocket = new LocalPlayerSocket("player_id", mMockSocket, mMockSocketFactory);

        localPlayerSocket.fire(10.0d, 20.0d, 100.0d);

        JSONObject message = new JSONObject();
        message.put("player_id", "player_id");
        message.put("lat", 10.0d);
        message.put("long", 20.0d);
        message.put("speed", 100.0d);

        verify(mMockSocket).emit(Matchers.eq("fire"), Matchers.refEq(message));
    }

    @Test
    public void testThatThePlayerSendAMoveMessageViaSocketWhenMoveMethodIsCalled() throws JSONException {

        LocalPlayerSocket localPlayerSocket = new LocalPlayerSocket("player_id", mMockSocket, mMockSocketFactory);

        localPlayerSocket.move(10.0d, 20.0d);

        JSONObject message = new JSONObject();
        message.put("player_id", "player_id");
        message.put("lat", 10.0d);
        message.put("long", 20.0d);

        verify(mMockSocket).emit(Matchers.eq("move"), Matchers.refEq(message));
    }

    @Test
    public void testThatThePlayerSendALeaveMessageViaSocketWhenLeaveMethodIsCalled() throws JSONException {

        LocalPlayerSocket localPlayerSocket = new LocalPlayerSocket("player_id", mMockSocket, mMockSocketFactory);

        localPlayerSocket.leave();

        JSONObject message = new JSONObject();
        message.put("player_id", "player_id");

        verify(mMockSocket).emit(Matchers.eq("player_leave"), Matchers.refEq(message));
    }
}
