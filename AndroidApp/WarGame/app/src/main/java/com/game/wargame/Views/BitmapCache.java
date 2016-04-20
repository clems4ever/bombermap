package com.game.wargame.Views;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.game.wargame.R;
import com.game.wargame.Views.Animations.Animation;
import com.game.wargame.Views.Animations.IAnimationFactory;
import com.game.wargame.Views.Animations.IBitmapFactory;
import com.game.wargame.Views.Animations.PlayerAliveAnimation;
import com.game.wargame.Views.Animations.Size;
import com.game.wargame.Views.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.BitmapDescriptor;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by sergei on 14/03/16.
 */
public class BitmapCache {
    private HashMap<Integer, BitmapDescriptor> mBitmaps;
    private IAnimationFactory mAnimationFactory;
    private IBitmapFactory mBitmapFactory;
    private Resources mResources;

    public BitmapDescriptor getBitmap(int resourceID)
    {
        return mBitmaps.get(resourceID);
    }

    public BitmapCache(Resources resources, IAnimationFactory animationFactory, IBitmapFactory bitmapFactory) {
        mAnimationFactory = animationFactory;
        mBitmapFactory = bitmapFactory;
        mResources = resources;
    }

    public void loadBitmaps() {
        //Load all the bitmaps necessary for all animations in memory before the game
        mBitmaps = new HashMap<>();
        ArrayList<Animation> animations = mAnimationFactory.buildAllAnimations();
        for (Animation animation : animations)
        {
            mBitmaps.put(animation.current(), mBitmapFactory.load(animation.current(), animation.SIZE));
            while (animation.hasNext()) {
                animation.next();
                int resourceKey = animation.current();
                mBitmaps.put(resourceKey, mBitmapFactory.load(resourceKey, animation.SIZE));
            }
        }

        mBitmaps.put(R.mipmap.wall, mBitmapFactory.load(R.mipmap.wall));

        Bitmap playerShadow = BitmapFactory.decodeResource(mResources, R.mipmap.profile_s);
        Bitmap transBitmap = Bitmap.createBitmap(playerShadow.getWidth(), playerShadow.getHeight(), Bitmap.Config.ARGB_8888);
        Paint paint = new Paint();
        paint.setAlpha(70);
        Canvas canvas = new Canvas(transBitmap);
        canvas.drawBitmap(playerShadow, 0, 0, paint);

        mBitmaps.put(1000, mBitmapFactory.load(transBitmap, PlayerAliveAnimation.SIZE));
    }

}
