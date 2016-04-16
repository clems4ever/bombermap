package com.game.wargame.Views;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.game.wargame.Views.GoogleMap.GoogleMap;
import com.game.wargame.Views.GoogleMap.GoogleMapView;
import com.game.wargame.Views.Views.MapView;
import com.google.android.gms.maps.model.LatLng;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by developer on 3/13/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class MapViewTest {

    @Mock private FragmentActivity mMockFragmentActivity;
    @Mock private GoogleMapView mMockGoogleMapView;
    @Mock private MapView.OnMapReadyListener mMockOnMapReadyListener;
    @Mock private Bundle mMockBundle;
    @Mock private GoogleMap mMockGoogleMap;
    @Mock private BitmapDescriptorFactory mMockBitmapDescriptorFactory;
    @Mock private PlayerMarkerFactory mMockPlayerMarkerFactory;
    @Mock private PlayerMarker mMockPlayerMarker;

    @Test
    public void testThatStartAsyncLoadsTheMap() {
        MapView mapView = new MapView(mMockFragmentActivity, mMockGoogleMapView, mMockBitmapDescriptorFactory, mMockPlayerMarkerFactory);

        mapView.startAsync(mMockOnMapReadyListener);

        verify(mMockGoogleMapView).getMapAsync(mapView);
    }

    @Test
    public void mapReady_callback_is_called_when_map_is_loaded() {
        ArgumentCaptor<GoogleMapView.OnMapReadyCallback> onMapReadyCallbackArgumentCaptor = ArgumentCaptor.forClass(GoogleMapView.OnMapReadyCallback.class);
        MapView mapView = new MapView(mMockFragmentActivity, mMockGoogleMapView, mMockBitmapDescriptorFactory, mMockPlayerMarkerFactory);

        // We request the service to load the map
        mapView.startAsync(mMockOnMapReadyListener);

        verify(mMockGoogleMapView).getMapAsync(onMapReadyCallbackArgumentCaptor.capture());

        GoogleMapView.OnMapReadyCallback onMapReadyCallback = onMapReadyCallbackArgumentCaptor.getValue();

        onMapReadyCallback.onMapReady(mMockGoogleMap);

        verify(mMockOnMapReadyListener).onMapReady();
    }

    @Test
    public void adding_local_user_add_marker_on_googleMap() {
        ArgumentCaptor<Runnable> UiThreadRunnableArgumentCaptor = ArgumentCaptor.forClass(Runnable.class);

        MapView mapView = new MapView(mMockFragmentActivity, mMockGoogleMapView, mMockBitmapDescriptorFactory, mMockPlayerMarkerFactory);

        ArgumentCaptor<GoogleMapView.OnMapReadyCallback> onMapReadyCallbackArgumentCaptor = ArgumentCaptor.forClass(GoogleMapView.OnMapReadyCallback.class);
        mapView.startAsync(mMockOnMapReadyListener);
        verify(mMockGoogleMapView).getMapAsync(onMapReadyCallbackArgumentCaptor.capture());
        GoogleMapView.OnMapReadyCallback onMapReadyCallback = onMapReadyCallbackArgumentCaptor.getValue();
        onMapReadyCallback.onMapReady(mMockGoogleMap);

        mapView.addLocalPlayer("player_id");

        verify(mMockFragmentActivity).runOnUiThread(UiThreadRunnableArgumentCaptor.capture());

        Runnable uiTask = UiThreadRunnableArgumentCaptor.getValue();
        uiTask.run();

        verify(mMockPlayerMarkerFactory).create(MapView.LOCAL_PLAYER_MARKER_RES_ID);
    }

    @Test
    public void adding_remote_user_add_marker_on_googleMap() {
        ArgumentCaptor<Runnable> UiThreadRunnableArgumentCaptor = ArgumentCaptor.forClass(Runnable.class);

        MapView mapView = new MapView(mMockFragmentActivity, mMockGoogleMapView, mMockBitmapDescriptorFactory, mMockPlayerMarkerFactory);

        ArgumentCaptor<GoogleMapView.OnMapReadyCallback> onMapReadyCallbackArgumentCaptor = ArgumentCaptor.forClass(GoogleMapView.OnMapReadyCallback.class);
        mapView.startAsync(mMockOnMapReadyListener);
        verify(mMockGoogleMapView).getMapAsync(onMapReadyCallbackArgumentCaptor.capture());
        GoogleMapView.OnMapReadyCallback onMapReadyCallback = onMapReadyCallbackArgumentCaptor.getValue();
        onMapReadyCallback.onMapReady(mMockGoogleMap);

        mapView.addRemotePlayer("player_id");

        verify(mMockFragmentActivity).runOnUiThread(UiThreadRunnableArgumentCaptor.capture());

        Runnable uiTask = UiThreadRunnableArgumentCaptor.getValue();
        uiTask.run();

        verify(mMockPlayerMarkerFactory).create(MapView.REMOTE_PLAYER_MARKER_RES_ID);
    }

    @Test
    public void move_a_player_that_has_been_previously_added()
    {
        when(mMockPlayerMarkerFactory.create(anyInt())).thenReturn(mMockPlayerMarker);

        MapView mapView = new MapView(mMockFragmentActivity, mMockGoogleMapView, mMockBitmapDescriptorFactory, mMockPlayerMarkerFactory);

        mapView.startAsync(mMockOnMapReadyListener);
        mapView.onMapReady(mMockGoogleMap);

        // Add the player to the view
        mapView.addRemotePlayer("player_id");

        // Transmit the request to the UI
        ArgumentCaptor<Runnable> captor1 = ArgumentCaptor.forClass(Runnable.class);
        verify(mMockFragmentActivity).runOnUiThread(captor1.capture());
        Runnable uiTask1 = captor1.getValue();
        uiTask1.run();

        // Move the player to (4,6)
        LatLng newPosition = new LatLng(4, 6);
        mapView.movePlayerTo("player_id", newPosition);

        // Transmit the request to the UI
        ArgumentCaptor<Runnable> captor2 = ArgumentCaptor.forClass(Runnable.class);
        verify(mMockFragmentActivity, times(2)).runOnUiThread(captor2.capture());
        Runnable uiTask2 = captor2.getValue();
        uiTask2.run();

        // Check that the move has been done
        verify(mMockPlayerMarker).move(newPosition);
    }

    @Test
    public void remove_a_player_that_has_been_previously_added() {
        when(mMockPlayerMarkerFactory.create(anyInt())).thenReturn(mMockPlayerMarker);

        MapView mapView = new MapView(mMockFragmentActivity, mMockGoogleMapView, mMockBitmapDescriptorFactory, mMockPlayerMarkerFactory);

        mapView.startAsync(mMockOnMapReadyListener);
        mapView.onMapReady(mMockGoogleMap);

        // Add the player to the view
        mapView.addRemotePlayer("player_id");

        // Transmit the request to the UI
        ArgumentCaptor<Runnable> captor1 = ArgumentCaptor.forClass(Runnable.class);
        verify(mMockFragmentActivity).runOnUiThread(captor1.capture());
        Runnable uiTask1 = captor1.getValue();
        uiTask1.run();

        // Move the player to (4,6)
        LatLng newPosition = new LatLng(4, 6);
        mapView.removePlayer("player_id");

        // Transmit the request to the UI
        ArgumentCaptor<Runnable> captor2 = ArgumentCaptor.forClass(Runnable.class);
        verify(mMockFragmentActivity, times(2)).runOnUiThread(captor2.capture());
        Runnable uiTask2 = captor2.getValue();
        uiTask2.run();

        // Check that the removing has been done
        verify(mMockPlayerMarker).remove();
    }
}
