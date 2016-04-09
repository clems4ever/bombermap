package com.game.wargame.Views;

import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;

/**
 * Created by clement on 03/04/16.
 */
public interface IGoogleMapView {

    public void onCreate(Bundle bundle);
    public void onResume();

    public void getMapAsync(final OnMapReadyCallback onMapReadyCallback);

    public interface OnMapReadyCallback {
        public void onMapReady(GoogleMap googleMap);
    }
}
