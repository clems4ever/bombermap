package com.game.wargame.Views;

import android.os.Bundle;

import com.google.android.gms.maps.MapView;

/**
 * Created by developer on 3/13/16.
 */
public class GoogleMapView implements IGoogleMapView {

    private MapView mMapView;

    public GoogleMapView(MapView mapView) {
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
            public void onMapReady(com.google.android.gms.maps.GoogleMap googleMap) {
                GoogleMap googleMapWrapper = new GoogleMap(googleMap);
                onMapReadyCallback.onMapReady(googleMapWrapper);
            }
        });
    }
}

