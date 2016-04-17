package com.game.wargame.Views.Animations;

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

    public BitmapDescriptor getBitmap(int resourceID)
    {
        return mBitmaps.get(resourceID);
    }

    public BitmapCache(IAnimationFactory animationFactory, IBitmapFactory bitmapFactory) {
        mAnimationFactory = animationFactory;
        mBitmapFactory = bitmapFactory;
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
    }

}
