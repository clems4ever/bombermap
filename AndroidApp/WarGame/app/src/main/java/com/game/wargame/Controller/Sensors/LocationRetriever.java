package com.game.wargame.Controller.Sensors;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;


public class LocationRetriever extends AbstractLocationRetriever implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    protected GoogleApiClient mGoogleApiClient;
    protected LocationRequest mLocationRequest;
    private Location mCurrentLocation;
    private FusedLocationProviderApi mLocationServices;

    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 2000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    public LocationRetriever(Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        init(googleApiClient, LocationServices.FusedLocationApi);
    }

    public LocationRetriever(Context context, GoogleApiClient googleApiClient, FusedLocationProviderApi fusedLocationProviderApi) {
        init(googleApiClient, fusedLocationProviderApi);
    }

    public void init(GoogleApiClient googleApiClient, FusedLocationProviderApi fusedLocationProviderApi) {
        mGoogleApiClient = googleApiClient;
        mLocationServices = fusedLocationProviderApi;
        createLocationRequest();
    }

    public void start() {
        mGoogleApiClient.connect();
    }

    public void stop() {
        mGoogleApiClient.disconnect();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (mCurrentLocation == null) {
            mCurrentLocation = mLocationServices.getLastLocation(mGoogleApiClient);
        }
        startListeningLocationUpdates();
    }

    protected void startListeningLocationUpdates() {
        mLocationServices.requestLocationUpdates(mGoogleApiClient, mLocationRequest, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if(mOnLocationRetrievedListener != null) {
                    mOnLocationRetrievedListener.onLocationRetrieved(location.getLatitude(), location.getLongitude());
                }
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }
}
