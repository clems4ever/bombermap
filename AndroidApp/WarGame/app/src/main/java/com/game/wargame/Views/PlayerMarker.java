package com.game.wargame.Views;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by clement on 03/04/16.
 */
public class PlayerMarker {

    private GroundOverlay mMarker;

    public PlayerMarker(GroundOverlay groundOverlay) {
        mMarker = groundOverlay;
    }

    public void move(LatLng to) {
        if (mMarker != null)
            mMarker.setPosition(to);
    }

    public void setIcon(BitmapDescriptor bitmap) {
        if (mMarker != null)
            mMarker.setImage(bitmap);
    }

    public void remove() {
        if (mMarker != null)
            mMarker.remove();
        mMarker = null;
    }

}
