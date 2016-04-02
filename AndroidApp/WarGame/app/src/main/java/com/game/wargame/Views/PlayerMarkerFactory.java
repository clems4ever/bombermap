package com.game.wargame.Views;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by clement on 03/04/16.
 */
public class PlayerMarkerFactory {

    private GoogleMapWrapper mGoogleMapWrapper;
    private BitmapDescriptorFactory mBitmapDescriptorFactory;

    public PlayerMarkerFactory(GoogleMapWrapper googleMapWrapper, BitmapDescriptorFactory bitmapDescriptorFactory) {
        mGoogleMapWrapper = googleMapWrapper;
        mBitmapDescriptorFactory = bitmapDescriptorFactory;
    }

    public PlayerMarker create(int bitmapResId) {
        BitmapDescriptor bmp = mBitmapDescriptorFactory.fromResource(bitmapResId);
        GroundOverlay marker = mGoogleMapWrapper.addGroundOverlay(new GroundOverlayOptions()
                .position(new LatLng(0, 0), 400000, 400000)
                .anchor(0.5f, 0.35f)
                .zIndex(100)
                .image(bmp));

        return new PlayerMarker(marker);
    }

}
