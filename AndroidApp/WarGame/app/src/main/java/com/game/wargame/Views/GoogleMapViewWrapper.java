package com.game.wargame.Views;

import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.MapView;

/**
 * Created by developer on 3/13/16.
 */
public class GoogleMapViewWrapper {

    private MapView mMapView;

    public GoogleMapViewWrapper(MapView mapView) {
        mMapView = mapView;
    }

    public void onCreate(Bundle bundle) {
        mMapView.onCreate(bundle);
    }

    public void onResume() {
        mMapView.onResume();
    }

    public void getMapAsync(final OnMapReadyCallback onMapReadyCallback) {
        mMapView.getMapAsync(new com.google.android.gms.maps.OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                GoogleMapWrapper googleMapWrapper = new GoogleMapWrapper(googleMap);
                onMapReadyCallback.onMapReady(googleMapWrapper);
            }
        });
    }

    public interface OnMapReadyCallback {
        public void onMapReady(GoogleMapWrapper googleMap);
    }

}
