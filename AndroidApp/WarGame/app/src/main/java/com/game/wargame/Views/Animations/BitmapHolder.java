package com.game.wargame.Views.Animations;

import com.game.wargame.Views.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.BitmapDescriptor;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by sergei on 14/03/16.
 */
public class BitmapHolder {
    private HashMap<Integer, BitmapDescriptor> mBitmaps;

    public BitmapDescriptor getBitmap(int resourceID)
    {
        return mBitmaps.get(resourceID);
    }

    public BitmapHolder(BitmapDescriptorFactory bitmapDescriptorFactory) {
        //Load all the bitmaps necessary for all animations in memory before the game
        mBitmaps = new HashMap<>();
        ArrayList<Animation> animations = AnimationFactory.buildAllAnimations();
        for (Animation animation : animations)
        {
            mBitmaps.put(animation.current(), bitmapDescriptorFactory.fromResource(animation.current()));
            while (animation.hasNext()) {
                animation.next();
                int resourceKey = animation.current();
                mBitmaps.put(resourceKey, bitmapDescriptorFactory.fromResource(resourceKey));
            }
        }
    }

}
