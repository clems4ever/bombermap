package com.game.wargame.Controller;

import com.game.wargame.Controller.Communication.Game.RemotePlayerSocket;
import com.game.wargame.Model.Entities.OnPlayerPositionChangedListener;
import com.game.wargame.Model.Entities.OnPlayerWeaponTriggeredListener;
import com.game.wargame.Model.Entities.RemotePlayerModel;
import com.google.android.gms.maps.model.LatLng;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class RemotePlayerTest {

    @Mock
    private RemotePlayerSocket mMockRemotePlayerSocket;

    @Mock
    private OnPlayerPositionChangedListener mMockOnPlayerPositionChangedListener;
    @Mock
    private OnPlayerWeaponTriggeredListener mMockOnPlayerWeaponTriggeredListener;


    @Test
    public void testThatWhenLocationIsUpdatedPlayerUpdatesHisPosition() {

        RemotePlayerModel playerModel = new RemotePlayerModel("player_name", mMockRemotePlayerSocket);

        LatLng initialPosition = new LatLng(10, 20);
        playerModel.setPosition(initialPosition);

        playerModel.onMoveEvent(30, 40);
        LatLng newPosition = new LatLng(30, 40);

        assertEquals(playerModel.getPosition(), newPosition);
    }

    @Test
    public void testThatWhenLocationIsUpdatedPlayerCallsItsSubscriber() {

        RemotePlayerModel localPlayerModel = new RemotePlayerModel("player_name", mMockRemotePlayerSocket);

        LatLng initialPosition = new LatLng(10, 20);
        localPlayerModel.setPosition(initialPosition);
        localPlayerModel.setOnPlayerPositionChangedListener(mMockOnPlayerPositionChangedListener);

        localPlayerModel.onMoveEvent(30, 40);

        verify(mMockOnPlayerPositionChangedListener).onPlayerPositionChanged(localPlayerModel);
    }

    @Test
    public void testThatWhenFireIsTriggeredPlayerCallsItsSubscriber() {

        RemotePlayerModel localPlayerModel = new RemotePlayerModel("player_name", mMockRemotePlayerSocket);

        LatLng initialPosition = new LatLng(10, 20);
        localPlayerModel.setPosition(initialPosition);
        localPlayerModel.setOnPlayerWeaponTriggeredListener(mMockOnPlayerWeaponTriggeredListener);

        localPlayerModel.onFireEvent(30, 40, 10);

        verify(mMockOnPlayerWeaponTriggeredListener).onPlayerWeaponTriggeredListener(localPlayerModel, 30, 40, 10);
    }
}
