package com.game.wargame.Views;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by clement on 03/04/16.
 */
public class PlayerMarkerFactory {

    private GoogleMap mGoogleMap;
    private BitmapDescriptorFactory mBitmapDescriptorFactory;

    public PlayerMarkerFactory(GoogleMap googleMap, BitmapDescriptorFactory bitmapDescriptorFactory) {
        mGoogleMap = googleMap;
        mBitmapDescriptorFactory = bitmapDescriptorFactory;
    }

    public PlayerMarker create(int bitmapResId) {
        BitmapDescriptor bmp = mBitmapDescriptorFactory.load(bitmapResId);
        return mGoogleMap.addPlayerMarker(new GroundOverlayOptions()
                .position(new LatLng(0, 0), 20, 20)
                .anchor(0.5f, 0.35f)
                .zIndex(-100)
                .image(bmp));
    }

}
