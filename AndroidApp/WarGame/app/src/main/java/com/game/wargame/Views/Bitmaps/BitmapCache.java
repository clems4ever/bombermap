package com.game.wargame.Views.Bitmaps;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.game.wargame.R;
import com.game.wargame.Views.Animations.Animation;
import com.game.wargame.Views.Animations.IAnimationFactory;
import com.game.wargame.Views.Animations.PlayerAliveAnimation;
import com.google.android.gms.maps.model.BitmapDescriptor;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by sergei on 14/03/16.
 */
public class BitmapCache {
    private HashMap<Integer, BitmapDescriptor> mBitmaps;
    private IAnimationFactory mAnimationFactory;
    private IBitmapDescriptorFactory mBitmapDescriptorFactory;
    private IBitmapFactory mBitmapFactory;
    private Resources mResources;

    public BitmapCache(Resources resources, IAnimationFactory animationFactory, IBitmapDescriptorFactory bitmapDescriptorFactory) {
        init(resources, animationFactory, bitmapDescriptorFactory, new BitmapFactory());
    }

    // TEST
    public BitmapCache(Resources resources, IAnimationFactory animationFactory, IBitmapDescriptorFactory bitmapDescriptorFactory, IBitmapFactory bitmapFactory) {
        init(resources, animationFactory, bitmapDescriptorFactory, bitmapFactory);
    }

    private void init(Resources resources, IAnimationFactory animationFactory, IBitmapDescriptorFactory bitmapDescriptorFactory, IBitmapFactory bitmapFactory) {
        mAnimationFactory = animationFactory;
        mBitmapDescriptorFactory = bitmapDescriptorFactory;
        mBitmapFactory = bitmapFactory;
        mResources = resources;
    }

    public BitmapDescriptor getBitmap(int resourceID)
    {
        return mBitmaps.get(resourceID);
    }

    public void loadBitmaps() {
        //Load all the bitmaps necessary for all animations in memory before the game
        mBitmaps = new HashMap<>();
        ArrayList<Animation> animations = mAnimationFactory.buildAllAnimations();
        for (Animation animation : animations)
        {
            mBitmaps.put(animation.current(), mBitmapDescriptorFactory.load(animation.current(), animation.getSize()));
            while (animation.hasNext()) {
                animation.next();
                int resourceKey = animation.current();
                mBitmaps.put(resourceKey, mBitmapDescriptorFactory.load(resourceKey, animation.getSize()));
            }
        }

        mBitmaps.put(R.mipmap.wall, mBitmapDescriptorFactory.load(R.mipmap.wall));

        Bitmap playerShadow = mBitmapFactory.decodeResource(mResources, R.mipmap.profile_s);
        if(playerShadow != null) {
            Bitmap transBitmap = mBitmapFactory.createBitmap(playerShadow.getWidth(), playerShadow.getHeight(), Bitmap.Config.ARGB_8888);
            Paint paint = new Paint();
            paint.setAlpha(70);
            Canvas canvas = new Canvas(transBitmap);
            canvas.drawBitmap(playerShadow, 0, 0, paint);

            mBitmaps.put(1000, mBitmapDescriptorFactory.load(transBitmap, PlayerAliveAnimation.SIZE));
        }
    }

}
