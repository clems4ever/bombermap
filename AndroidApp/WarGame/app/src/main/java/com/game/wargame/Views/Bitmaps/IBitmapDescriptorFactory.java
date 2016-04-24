package com.game.wargame.Views.Bitmaps;

import android.graphics.Bitmap;

import com.game.wargame.Views.Animations.Size;
import com.google.android.gms.maps.model.BitmapDescriptor;

/**
 * Created by sergei on 20/03/16.
 */
public interface IBitmapDescriptorFactory {
    public BitmapDescriptor load(int id);
    public BitmapDescriptor load(int id, Size size);
    public BitmapDescriptor load(Bitmap bitmap);
    public BitmapDescriptor load(Bitmap bitmap, Size size);
}
