package com.game.wargame.Views.GoogleMap;

import android.view.View;

import com.game.wargame.R;

/**
 * Created by clement on 03/04/16.
 */
public class GoogleMapViewFactory {

    public GoogleMapViewFactory() {
    }

    public IGoogleMapView create(View view) {
        com.google.android.gms.maps.MapView mapView = (com.google.android.gms.maps.MapView) view.findViewById(R.id.map);
        return new GoogleMapView(mapView);
    }

}
