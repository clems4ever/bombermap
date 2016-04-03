package com.game.wargame.Views.Animations;

import com.google.android.gms.maps.model.BitmapDescriptor;

import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

/**
 * Created by sergei on 20/03/16.
 */

public class BitmapCacheTest {

    @Mock
    BitmapDescriptor mBitmapDescriptor1;

    @Mock
    BitmapDescriptor mBitmapDescriptor2;

    @Mock
    BitmapDescriptor mBitmapDescriptor3;

    public class DummyAnimation extends Animation {
        public DummyAnimation() {
            mDrawablesId.add(1);
            mDrawablesId.add(2);
            mDrawablesId.add(3);
            mFrameRateMillis = 50;
        }
    }

    public class DummyBitmapFactory implements IBitmapFactory {

        @Override
        public BitmapDescriptor load(int id) {
            switch (id) {
                case 1:
                    return mBitmapDescriptor1;
                case 2:
                    return mBitmapDescriptor2;
                case 3:
                    return mBitmapDescriptor3;
            }
            return mBitmapDescriptor1;
        }
    }

    public class DummyAnimationFactory implements IAnimationFactory {
        @Override
        public ArrayList<Animation> buildAllAnimations () {
            ArrayList<Animation> animations = new ArrayList<>();
            Animation animation = new DummyAnimation();
            animation.first();
            animations.add(animation);
            return animations;
        }
    }

    @Test
    public void testThatBitmapHolderContainsAllBitmapsDescriptors() {
        BitmapCache bitmapCache = new BitmapCache(new DummyAnimationFactory(), new DummyBitmapFactory());
        bitmapCache.loadBitmaps();

        BitmapDescriptor bitmapDescriptor = bitmapCache.getBitmap(1);
        assertEquals(mBitmapDescriptor1, bitmapDescriptor);

        bitmapDescriptor = bitmapCache.getBitmap(2);
        assertEquals(mBitmapDescriptor2, bitmapDescriptor);

        bitmapDescriptor = bitmapCache.getBitmap(3);
        assertEquals(mBitmapDescriptor3, bitmapDescriptor);

    }


}
