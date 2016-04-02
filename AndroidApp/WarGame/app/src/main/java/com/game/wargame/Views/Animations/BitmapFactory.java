package com.game.wargame.Views.Animations;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

/**
 * Created by sergei on 20/03/16.
 */
public class BitmapFactory implements IBitmapFactory {
    @Override
    public BitmapDescriptor load(int id) {
        return BitmapDescriptorFactory.fromResource(id);
    }
}
