package com.game.wargame.Views;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.MarkerOptions;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by developer on 3/13/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class MapViewTest {

    @Mock private FragmentActivity mMockFragmentActivity;
    @Mock private GoogleMapViewWrapper mMockGoogleMapView;
    @Mock private MapView.OnMapReadyListener mMockOnMapReadyListener;
    @Mock private Bundle mMockBundle;
    @Mock private GoogleMapWrapper mMockGoogleMap;
    @Mock private BitmapDescriptorFactory mMockBitmapDescriptorFactory;

    @Test
    public void testThatStartAsyncLoadsTheMap() {
        MapView mapView = new MapView(mMockFragmentActivity, mMockGoogleMapView, mMockBitmapDescriptorFactory);

        mapView.startAsync(mMockOnMapReadyListener);

        verify(mMockGoogleMapView).getMapAsync(mapView);
    }

    @Test
    public void testThatCallbackIsCalledWhenMapIsLoaded() {
        ArgumentCaptor<GoogleMapViewWrapper.OnMapReadyCallback> onMapReadyCallbackArgumentCaptor = ArgumentCaptor.forClass(GoogleMapViewWrapper.OnMapReadyCallback.class);
        MapView mapView = new MapView(mMockFragmentActivity, mMockGoogleMapView, mMockBitmapDescriptorFactory);

        // We request the service to load the map
        mapView.startAsync(mMockOnMapReadyListener);

        verify(mMockGoogleMapView).getMapAsync(onMapReadyCallbackArgumentCaptor.capture());

        GoogleMapViewWrapper.OnMapReadyCallback onMapReadyCallback = onMapReadyCallbackArgumentCaptor.getValue();

        onMapReadyCallback.onMapReady(mMockGoogleMap);

        verify(mMockOnMapReadyListener).onMapReady();
    }

    @Test
    public void testThatAddingLocalUserAddMarkerOnGoogleMap() {
        ArgumentCaptor<Runnable> UiThreadRunnableArgumentCaptor = ArgumentCaptor.forClass(Runnable.class);

        MapView mapView = new MapView(mMockFragmentActivity, mMockGoogleMapView, mMockBitmapDescriptorFactory);

        ArgumentCaptor<GoogleMapViewWrapper.OnMapReadyCallback> onMapReadyCallbackArgumentCaptor = ArgumentCaptor.forClass(GoogleMapViewWrapper.OnMapReadyCallback.class);
        mapView.startAsync(mMockOnMapReadyListener);
        verify(mMockGoogleMapView).getMapAsync(onMapReadyCallbackArgumentCaptor.capture());
        GoogleMapViewWrapper.OnMapReadyCallback onMapReadyCallback = onMapReadyCallbackArgumentCaptor.getValue();
        onMapReadyCallback.onMapReady(mMockGoogleMap);

        mapView.addLocalPlayer("player_id");

        verify(mMockFragmentActivity).runOnUiThread(UiThreadRunnableArgumentCaptor.capture());

        Runnable uiTask = UiThreadRunnableArgumentCaptor.getValue();
        uiTask.run();

        verify(mMockGoogleMap).addMarker(Matchers.<MarkerOptions>any());
        verify(mMockBitmapDescriptorFactory).fromResource(MapView.LOCAL_PLAYER_MARKER_RES_ID);
    }

    @Test
    public void testThatAddingRemoteUserAddMarkerOnGoogleMap() {
        ArgumentCaptor<Runnable> UiThreadRunnableArgumentCaptor = ArgumentCaptor.forClass(Runnable.class);

        MapView mapView = new MapView(mMockFragmentActivity, mMockGoogleMapView, mMockBitmapDescriptorFactory);

        ArgumentCaptor<GoogleMapViewWrapper.OnMapReadyCallback> onMapReadyCallbackArgumentCaptor = ArgumentCaptor.forClass(GoogleMapViewWrapper.OnMapReadyCallback.class);
        mapView.startAsync(mMockOnMapReadyListener);
        verify(mMockGoogleMapView).getMapAsync(onMapReadyCallbackArgumentCaptor.capture());
        GoogleMapViewWrapper.OnMapReadyCallback onMapReadyCallback = onMapReadyCallbackArgumentCaptor.getValue();
        onMapReadyCallback.onMapReady(mMockGoogleMap);

        mapView.addRemotePlayer("player_id");

        verify(mMockFragmentActivity).runOnUiThread(UiThreadRunnableArgumentCaptor.capture());

        Runnable uiTask = UiThreadRunnableArgumentCaptor.getValue();
        uiTask.run();

        verify(mMockGoogleMap).addMarker(Matchers.<MarkerOptions>any());
        verify(mMockBitmapDescriptorFactory).fromResource(MapView.REMOTE_PLAYER_MARKER_RES_ID);
    }
}
