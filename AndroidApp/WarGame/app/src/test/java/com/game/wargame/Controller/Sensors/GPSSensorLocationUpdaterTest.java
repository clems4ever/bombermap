package com.game.wargame.Controller.Sensors;

import android.content.Context;
import android.os.Bundle;
import android.location.Location;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GPSSensorLocationUpdaterTest {

    @Mock
    private Context mMockContext;

    @Mock
    private GoogleApiClient mMockGoogleApiClient;

    @Mock
    private FusedLocationProviderApi mMockFusedLocationProviderApi;

    @Mock
    private Location mMockLocation;

    @Mock
    private Bundle mMockBundle;

    @Mock
    private OnLocationRetrievedListener mMockOnLocationRetrievedListener;

    @Test
    public void testThatLocationRetrieverCallsTheGoogleApi() {
        GPSSensorLocationUpdater GPSSensorLocationUpdater = new GPSSensorLocationUpdater(mMockContext, mMockGoogleApiClient, mMockFusedLocationProviderApi);

        GPSSensorLocationUpdater.setOnLocationRetrievedListener(mMockOnLocationRetrievedListener);
        GPSSensorLocationUpdater.start();

        GPSSensorLocationUpdater.stop();

        verify(mMockGoogleApiClient, times(1)).connect();
        verify(mMockGoogleApiClient, times(1)).disconnect();
    }

    @Test
    public void testThatWhenConnectedListenerStartsListening() {
        GPSSensorLocationUpdater GPSSensorLocationUpdater = new GPSSensorLocationUpdater(mMockContext, mMockGoogleApiClient, mMockFusedLocationProviderApi);

        GPSSensorLocationUpdater.setOnLocationRetrievedListener(mMockOnLocationRetrievedListener);
        GPSSensorLocationUpdater.start();

        GPSSensorLocationUpdater.onConnected(mMockBundle);

        verify(mMockFusedLocationProviderApi).requestLocationUpdates(eq(mMockGoogleApiClient), Matchers.<LocationRequest>any(), Matchers.<LocationListener>any());
    }

    @Test
    public void testThatLocationUpdateListenerIsCalledWhenTheServiceNotifiesAnUpdate() {
        ArgumentCaptor<LocationListener> captor = ArgumentCaptor.forClass(LocationListener.class);
        GPSSensorLocationUpdater GPSSensorLocationUpdater = new GPSSensorLocationUpdater(mMockContext, mMockGoogleApiClient, mMockFusedLocationProviderApi);

        GPSSensorLocationUpdater.setOnLocationRetrievedListener(mMockOnLocationRetrievedListener);
        GPSSensorLocationUpdater.start();

        GPSSensorLocationUpdater.onConnected(mMockBundle);

        verify(mMockFusedLocationProviderApi).requestLocationUpdates(eq(mMockGoogleApiClient), Matchers.<LocationRequest>any(), captor.capture());

        LocationListener listener = captor.getValue();

        when(mMockLocation.getLatitude()).thenReturn(5.0d);
        when(mMockLocation.getLongitude()).thenReturn(10.0d);

        listener.onLocationChanged(mMockLocation);

        verify(mMockOnLocationRetrievedListener).onLocationRetrieved(eq(5.0d), eq(10.0d));
    }

    @Test
    public void testReconnectionWhenServiceConnectionIsSuspended() {
        GPSSensorLocationUpdater GPSSensorLocationUpdater = new GPSSensorLocationUpdater(mMockContext, mMockGoogleApiClient, mMockFusedLocationProviderApi);

        GPSSensorLocationUpdater.setOnLocationRetrievedListener(mMockOnLocationRetrievedListener);
        GPSSensorLocationUpdater.start();
        GPSSensorLocationUpdater.onConnectionSuspended(5);

        verify(mMockGoogleApiClient, times(2)).connect();
    }

}
