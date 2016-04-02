package com.game.wargame.Views;

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
        mMarker.setPosition(to);
    }

    public void remove() {
        mMarker.remove();
        mMarker = null;
    }

}
