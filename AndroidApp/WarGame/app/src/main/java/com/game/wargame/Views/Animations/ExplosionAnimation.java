package com.game.wargame.Views.Animations;

import com.game.wargame.R;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by sergei on 14/03/16.
 */
public class ExplosionAnimation extends Animation {

    public static final Size SIZE = new Size(128, 128);

    public ExplosionAnimation() {
        mDrawablesId.add(R.mipmap.explosion);
        mDrawablesId.add(R.mipmap.explosion1);
        mDrawablesId.add(R.mipmap.explosion2);
        mDrawablesId.add(R.mipmap.explosion3);
        mDrawablesId.add(R.mipmap.explosion4);
        mDrawablesId.add(R.mipmap.explosion5);
        mDrawablesId.add(R.mipmap.explosion6);
        mDrawablesId.add(R.mipmap.explosion7);
    }
}
