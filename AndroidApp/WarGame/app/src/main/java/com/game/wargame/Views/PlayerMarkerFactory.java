package com.game.wargame.Views;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.game.wargame.Views.Animations.PlayerAliveAnimation;
import com.game.wargame.Views.GoogleMap.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by clement on 03/04/16.
 */
public class PlayerMarkerFactory {

    private GoogleMap mGoogleMap;
    private BitmapDescriptorFactory mBitmapDescriptorFactory;
    private Resources mResources;

    public PlayerMarkerFactory(BitmapDescriptorFactory bitmapDescriptorFactory, Resources res) {
        mBitmapDescriptorFactory = bitmapDescriptorFactory;
        mResources = res;
    }

    public void setGoogleMap(GoogleMap googleMap) {
        mGoogleMap = googleMap;
    }

    public PlayerMarker create(int bitmapResId) {
        BitmapDescriptor bitmapDescriptor = mBitmapDescriptorFactory.load(bitmapResId, PlayerAliveAnimation.SIZE);
        return mGoogleMap.addPlayerMarker(new MarkerOptions()
                .position(new LatLng(0, 0))
                .anchor(0.5f, 0.5f)
                .icon(bitmapDescriptor));
    }

}
