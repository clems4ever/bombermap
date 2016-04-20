package com.game.wargame.Views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.game.wargame.Views.Activities.GameMainFragment;
import com.game.wargame.Views.Animations.Animation;
import com.game.wargame.Views.Animations.IBitmapFactory;
import com.game.wargame.Views.Animations.Size;
import com.google.android.gms.maps.model.BitmapDescriptor;

/**
 * Created by developer on 3/13/16.
 */
public class BitmapDescriptorFactory implements IBitmapFactory {

    private Context mContext;

    public BitmapDescriptorFactory(Context context) {
        mContext = context;
    }

    @Override
    public BitmapDescriptor load(int resId, Size size) {
        Bitmap bmp = BitmapFactory.decodeResource(mContext.getResources(), resId);
        bmp = Bitmap.createScaledBitmap(bmp, size.x, size.y, false);
        return com.google.android.gms.maps.model.BitmapDescriptorFactory.fromBitmap(bmp);
    }

    @Override
    public BitmapDescriptor load(int resId) {
        return com.google.android.gms.maps.model.BitmapDescriptorFactory.fromResource(resId);
    }

    @Override
    public BitmapDescriptor load(Bitmap bitmap) {
        return com.google.android.gms.maps.model.BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public BitmapDescriptor load(Bitmap bitmap, Size size) {
        Bitmap scaled = Bitmap.createScaledBitmap(bitmap, size.x, size.y, false);
        return com.google.android.gms.maps.model.BitmapDescriptorFactory.fromBitmap(scaled);
    }

}
