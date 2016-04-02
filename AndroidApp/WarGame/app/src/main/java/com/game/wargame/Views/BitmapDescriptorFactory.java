package com.game.wargame.Views;

import com.game.wargame.Views.Animations.IBitmapFactory;
import com.google.android.gms.maps.model.BitmapDescriptor;

/**
 * Created by developer on 3/13/16.
 */
public class BitmapDescriptorFactory implements IBitmapFactory {

    public BitmapDescriptorFactory() {
    }

    public BitmapDescriptor load(int resId) {
        return com.google.android.gms.maps.model.BitmapDescriptorFactory.fromResource(resId);
    }

}
