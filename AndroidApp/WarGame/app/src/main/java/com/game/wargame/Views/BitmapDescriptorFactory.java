package com.game.wargame.Views;

import com.google.android.gms.maps.model.BitmapDescriptor;

/**
 * Created by developer on 3/13/16.
 */
public class BitmapDescriptorFactory {

    public BitmapDescriptorFactory() {
    }

    public BitmapDescriptor fromResource(int resId) {
        return com.google.android.gms.maps.model.BitmapDescriptorFactory.fromResource(resId);
    }

}
