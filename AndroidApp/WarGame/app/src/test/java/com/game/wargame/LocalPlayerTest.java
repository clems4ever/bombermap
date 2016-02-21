package com.game.wargame;

import com.game.wargame.Controller.Communication.Communication.Communication.PlayerSocket;
import com.game.wargame.Model.Entities.LocalPlayerModel;
import com.game.wargame.Model.Entities.OnPlayerPositionChangedListener;
import com.game.wargame.Model.Entities.OnPlayerWeaponTriggeredListener;
import com.google.android.gms.maps.model.LatLng;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class LocalPlayerTest {

    @Mock
    private PlayerSocket mMockPlayerSocket;

    @Mock
    private OnPlayerPositionChangedListener mMockOnPlayerPositionChangedListener;
    @Mock
    private OnPlayerWeaponTriggeredListener mMockOnPlayerWeaponTriggeredListener;

    @Test
    public void testThatFireActionBroadcastTheEventToEveryOne() {

        LocalPlayerModel localPlayerModel = new LocalPlayerModel("player_name", mMockPlayerSocket);

        localPlayerModel.fire(50, 50, 10);

        verify(mMockPlayerSocket).fire(50, 50, 10);
    }

    @Test
    public void testThatWhenLocationIsUpdatedPlayerUpdatesHisPosition() {

        LocalPlayerModel localPlayerModel = new LocalPlayerModel("player_name", mMockPlayerSocket);

        LatLng initialPosition = new LatLng(10, 20);
        localPlayerModel.setPosition(initialPosition);

        localPlayerModel.onLocationUpdated(30, 40);
        LatLng newPosition = new LatLng(30, 40);

        assertEquals(localPlayerModel.getPosition(), newPosition);
    }

    @Test
    public void testThatWhenLocationIsUpdatedPlayerBroadcastsTheMoveToEveryone() {

        LocalPlayerModel localPlayerModel = new LocalPlayerModel("player_name", mMockPlayerSocket);

        LatLng initialPosition = new LatLng(10, 20);
        localPlayerModel.setPosition(initialPosition);

        localPlayerModel.onLocationUpdated(30, 40);

        verify(mMockPlayerSocket).move(30, 40);
    }

    @Test
    public void testThatWhenLocationIsUpdatedPlayerCallsItsSubscriber() {

        LocalPlayerModel localPlayerModel = new LocalPlayerModel("player_name", mMockPlayerSocket);

        LatLng initialPosition = new LatLng(10, 20);
        localPlayerModel.setPosition(initialPosition);
        localPlayerModel.setOnPlayerPositionChangedListener(mMockOnPlayerPositionChangedListener);

        localPlayerModel.onLocationUpdated(30, 40);

        verify(mMockOnPlayerPositionChangedListener).onPlayerPositionChanged(localPlayerModel);
    }

    @Test
    public void testThatWhenFireIsTriggeredPlayerBroadcastTheEventToEveryone() {

        LocalPlayerModel localPlayerModel = new LocalPlayerModel("player_name", mMockPlayerSocket);

        LatLng initialPosition = new LatLng(10, 20);
        localPlayerModel.setPosition(initialPosition);
        localPlayerModel.setOnPlayerWeaponTriggeredListener(mMockOnPlayerWeaponTriggeredListener);

        localPlayerModel.fire(30, 40, 10);

        verify(mMockPlayerSocket).fire(30, 40, 10);
    }

    @Test
    public void testThatWhenFireIsTriggeredPlayerCallsItsSubscriber() {

        LocalPlayerModel localPlayerModel = new LocalPlayerModel("player_name", mMockPlayerSocket);

        LatLng initialPosition = new LatLng(10, 20);
        localPlayerModel.setPosition(initialPosition);
        localPlayerModel.setOnPlayerWeaponTriggeredListener(mMockOnPlayerWeaponTriggeredListener);

        localPlayerModel.fire(30, 40, 10);

        verify(mMockOnPlayerWeaponTriggeredListener).onPlayerWeaponTriggeredListener(localPlayerModel, 30, 40, 10);
    }
}
