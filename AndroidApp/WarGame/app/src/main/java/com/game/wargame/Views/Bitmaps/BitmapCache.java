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
        mBitmaps = new HashMap<>();
    }

    public BitmapDescriptor getBitmap(int resourceID) throws Exception {
        if(!mBitmaps.containsKey(resourceID)) {
            throw new Exception("The bitmap " + resourceID + " does not exist in cache");
        }
        BitmapDescriptor descriptor = mBitmaps.get(resourceID);
        return descriptor;
    }

    public void add(int id, BitmapDescriptor bitmapDescriptor) {
        mBitmaps.put(id, bitmapDescriptor);
    }

    public void loadBitmaps() {
        //Load all the bitmaps necessary for all animations in memory before the game
        ArrayList<Animation> animations = mAnimationFactory.buildAllAnimations();
        for (Animation animation : animations)
        {
            mBitmaps.put(animation.current(), mBitmapDescriptorFactory.load(animation.current(), animation.SIZE));
            while (animation.hasNext()) {
                animation.next();
                int resourceKey = animation.current();
                mBitmaps.put(resourceKey, mBitmapDescriptorFactory.load(resourceKey, animation.SIZE));
            }
        }

        mBitmaps.put(R.mipmap.wall, mBitmapDescriptorFactory.load(R.mipmap.wall));
        mBitmaps.put(R.mipmap.woodbox, mBitmapDescriptorFactory.load(R.mipmap.woodbox));

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
