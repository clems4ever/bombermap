package com.game.wargame.Views;

import android.content.res.Resources;
import android.graphics.Bitmap;

import com.game.wargame.Views.Animations.PlayerAliveAnimation;
import com.game.wargame.Views.Bitmaps.BitmapDescriptorDescriptorFactory;
import com.game.wargame.Views.Bitmaps.IBitmapDescriptorFactory;
import com.game.wargame.Views.GoogleMap.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by clement on 03/04/16.
 */
public class PlayerMarkerFactory {

    private GoogleMap mGoogleMap;
    private IBitmapDescriptorFactory mBitmapDescriptorFactory;

    public PlayerMarkerFactory(IBitmapDescriptorFactory bitmapDescriptorFactory) {
        mBitmapDescriptorFactory = bitmapDescriptorFactory;
    }

    public void setGoogleMap(GoogleMap googleMap) {
        mGoogleMap = googleMap;
    }

    public PlayerMarker create(BitmapDescriptor descriptor) {
        return mGoogleMap.addPlayerMarker(new MarkerOptions()
                .position(new LatLng(0, 0))
                .anchor(0.5f, 0.5f)
                .icon(descriptor));
    }

    public PlayerMarker create(int bitmapResId) {
        BitmapDescriptor bitmapDescriptor = mBitmapDescriptorFactory.load(bitmapResId, PlayerAliveAnimation.SIZE);
        return create(bitmapDescriptor);
    }

    public PlayerMarker create(Bitmap bitmap) {
        BitmapDescriptor bitmapDescriptor = mBitmapDescriptorFactory.load(bitmap);
        return create(bitmapDescriptor);
    }
}
