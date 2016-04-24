package com.game.wargame.Views.Bitmaps;

import android.content.res.Resources;
import android.graphics.Bitmap;

/**
 * Created by clement on 24/04/16.
 */
public class BitmapFactory implements IBitmapFactory {

    public Bitmap decodeResource(Resources resources, int resId) {
        return android.graphics.BitmapFactory.decodeResource(resources, resId);
    }

    @Override
    public Bitmap createBitmap(int width, int height, Bitmap.Config config) {
        return Bitmap.createBitmap(width, height, config);
    }

}
