package com.game.wargame.Views.Bitmaps;

import android.content.res.Resources;
import android.graphics.Bitmap;

/**
 * Created by clement on 24/04/16.
 */
public interface IBitmapFactory {
    public Bitmap decodeResource(Resources resources, int resId);
    public Bitmap createBitmap(int width, int height, Bitmap.Config config);
}
