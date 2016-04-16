package com.game.wargame.Views;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by clement on 03/04/16.
 */
public class PlayerMarker {

    private Marker mMarker;

    public PlayerMarker(Marker marker) {
        mMarker = marker;
    }

    public void move(LatLng to) {
        if (mMarker != null)
            mMarker.setPosition(to);
    }

    public void setIcon(BitmapDescriptor bitmap) {
        if (mMarker != null)
            mMarker.setIcon(bitmap);
    }

    public void remove() {
        if (mMarker != null)
            mMarker.remove();
        mMarker = null;
    }

}
