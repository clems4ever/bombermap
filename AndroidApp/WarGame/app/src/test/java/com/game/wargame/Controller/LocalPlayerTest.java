package com.game.wargame.Controller;

import com.game.wargame.Controller.Communication.Game.LocalPlayerSocket;
import com.game.wargame.Controller.Communication.Game.PlayerSocket;
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
    private LocalPlayerSocket mMockLocalPlayerSocket;

    @Mock
    private OnPlayerPositionChangedListener mMockOnPlayerPositionChangedListener;
    @Mock
    private OnPlayerWeaponTriggeredListener mMockOnPlayerWeaponTriggeredListener;

    @Test
    public void testThatFireActionBroadcastTheEventToEveryOne() {

        LocalPlayerModel localPlayerModel = new LocalPlayerModel("player_name", mMockLocalPlayerSocket);

        localPlayerModel.fire(50, 50, 10);
        verify(mMockLocalPlayerSocket).fire(50, 50, 10);
    }

    @Test
    public void testThatWhenLocationIsUpdatedPlayerUpdatesHisPosition() {

        LocalPlayerModel localPlayerModel = new LocalPlayerModel("player_name", mMockLocalPlayerSocket);

        LatLng initialPosition = new LatLng(10, 20);
        localPlayerModel.setPosition(initialPosition);

        localPlayerModel.onLocationUpdated(30, 40);
        LatLng newPosition = new LatLng(30, 40);

        assertEquals(localPlayerModel.getPosition(), newPosition);
    }

    @Test
    public void testThatWhenLocationIsUpdatedPlayerBroadcastsTheMoveToEveryone() {

        LocalPlayerModel localPlayerModel = new LocalPlayerModel("player_name", mMockLocalPlayerSocket);

        LatLng initialPosition = new LatLng(10, 20);
        localPlayerModel.setPosition(initialPosition);

        localPlayerModel.onLocationUpdated(30, 40);

        verify(mMockLocalPlayerSocket).move(30, 40);
    }

    @Test
    public void testThatWhenLocationIsUpdatedPlayerCallsItsSubscriber() {

        LocalPlayerModel localPlayerModel = new LocalPlayerModel("player_name", mMockLocalPlayerSocket);

        LatLng initialPosition = new LatLng(10, 20);
        localPlayerModel.setPosition(initialPosition);
        localPlayerModel.setOnPlayerPositionChangedListener(mMockOnPlayerPositionChangedListener);

        localPlayerModel.onLocationUpdated(30, 40);
        verify(mMockOnPlayerPositionChangedListener).onPlayerPositionChanged(localPlayerModel);
    }

    @Test
    public void testThatWhenFireIsTriggeredPlayerBroadcastTheEventToEveryone() {

        LocalPlayerModel localPlayerModel = new LocalPlayerModel("player_name", mMockLocalPlayerSocket);

        LatLng initialPosition = new LatLng(10, 20);
        localPlayerModel.setPosition(initialPosition);
        localPlayerModel.setOnPlayerWeaponTriggeredListener(mMockOnPlayerWeaponTriggeredListener);

        localPlayerModel.fire(30, 40, 10);
        verify(mMockLocalPlayerSocket).fire(30, 40, 10);
    }

    @Test
    public void testThatWhenFireIsTriggeredPlayerCallsItsSubscriber() {

        LocalPlayerModel localPlayerModel = new LocalPlayerModel("player_name", mMockLocalPlayerSocket);

        LatLng initialPosition = new LatLng(10, 20);
        localPlayerModel.setPosition(initialPosition);
        localPlayerModel.setOnPlayerWeaponTriggeredListener(mMockOnPlayerWeaponTriggeredListener);

        localPlayerModel.fire(30, 40, 10);
        verify(mMockOnPlayerWeaponTriggeredListener).onPlayerWeaponTriggeredListener(localPlayerModel, 30, 40, 10);
    }

     @Test
    public void testThatWhenPlayerDiesPlayerBroadcastsEventToEveryone() {
         LocalPlayerModel localPlayerModel = new LocalPlayerModel("player_name", mMockLocalPlayerSocket);
         localPlayerModel.setPlayerId("player_id");
         double time = 0;
         localPlayerModel.die("killer_id", time);
         verify(mMockLocalPlayerSocket).die("player_id", "killer_id", time);
     }
}
