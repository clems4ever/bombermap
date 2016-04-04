package com.game.wargame.Model.Players;

import com.game.wargame.Controller.Communication.Game.RemotePlayerSocket;
import com.game.wargame.Model.Entities.Players.OnPlayerWeaponTriggeredListener;
import com.game.wargame.Model.Entities.Players.OnRemotePlayerPositionUpdated;
import com.game.wargame.Model.Entities.Players.RemotePlayerModel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class RemotePlayerModelTest {

    @Mock
    private RemotePlayerSocket mMockRemotePlayerSocket;

    @Mock
    private OnRemotePlayerPositionUpdated mMockOnPlayerPositionChangedListener;

    @Mock
    private OnPlayerWeaponTriggeredListener mMockOnPlayerWeaponTriggeredListener;

    @Test
    public void testThatMoveEventListenerIsCalledWhenSocketEventReceived() {

        ArgumentCaptor<RemotePlayerSocket.OnMoveEventListener> listener = ArgumentCaptor.forClass(RemotePlayerSocket.OnMoveEventListener.class);
        RemotePlayerModel remotePlayerModel = new RemotePlayerModel("player_id", mMockRemotePlayerSocket);

        // Set the listener to the player
        remotePlayerModel.setOnRemotePlayerPositionUpdated(mMockOnPlayerPositionChangedListener);

        verify(mMockRemotePlayerSocket).setOnMoveEventListener(listener.capture());

        listener.getValue().onMoveEvent(5.0f, 10.0f);

        verify(mMockOnPlayerPositionChangedListener).onRemotePlayerPositionChanged(Matchers.refEq(remotePlayerModel));
    }

    @Test
    public void testThatFireEventListenerIsCalledWhenSocketEventReceived() {

        ArgumentCaptor<RemotePlayerSocket.OnFireEventListener> listener = ArgumentCaptor.forClass(RemotePlayerSocket.OnFireEventListener.class);
        RemotePlayerModel remotePlayerModel = new RemotePlayerModel("player_id", mMockRemotePlayerSocket);

        // Set the listener to the player
        remotePlayerModel.setOnPlayerWeaponTriggeredListener(mMockOnPlayerWeaponTriggeredListener);

        verify(mMockRemotePlayerSocket).setOnFireEventListener(listener.capture());

        listener.getValue().onFireEvent(5.0d, 10.0d, 100.0d);

        verify(mMockOnPlayerWeaponTriggeredListener).onPlayerWeaponTriggeredListener(Matchers.refEq(remotePlayerModel), Matchers.eq(5.0d), Matchers.eq(10.0d), Matchers.eq(100.0d));
    }

}
