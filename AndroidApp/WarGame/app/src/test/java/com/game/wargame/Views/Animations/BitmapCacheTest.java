package com.game.wargame.Views.Animations;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.game.wargame.Views.Bitmaps.BitmapCache;
import com.game.wargame.Views.Bitmaps.IBitmapDescriptorFactory;
import com.game.wargame.Views.Bitmaps.IBitmapFactory;
import com.google.android.gms.maps.model.BitmapDescriptor;

import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

/**
 * Created by sergei on 20/03/16.
 */

public class BitmapCacheTest {

    @Mock private BitmapDescriptor mBitmapDescriptor1;
    @Mock private BitmapDescriptor mBitmapDescriptor2;
    @Mock private BitmapDescriptor mBitmapDescriptor3;
    @Mock private Resources mMockResources;
    @Mock private Bitmap mMockBitmap;
    @Mock private IBitmapFactory mMockBitmapFactory;

    public class DummyAnimation extends Animation {
        public DummyAnimation() {
            mDrawablesId.add(1);
            mDrawablesId.add(2);
            mDrawablesId.add(3);
            mFrameRateMillis = 50;
        }

        @Override
        public Size getSize() {
            return new Size(50, 50);
        }
    }

    public class DummyBitmapDescriptorFactory implements IBitmapDescriptorFactory {

        @Override
        public BitmapDescriptor load(int id, Size mSize) {
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

        @Override
        public BitmapDescriptor load(int id) {
            return null;
        }

        @Override
        public BitmapDescriptor load(Bitmap bitmap) {
            return null;
        }

        @Override
        public BitmapDescriptor load(Bitmap bitmap, Size size) {
            return null;
        }
    }

    public class MockBitmapFactory implements IBitmapFactory {

        @Override
        public Bitmap decodeResource(Resources resources, int resId) {
            return null;
        }

        @Override
        public Bitmap createBitmap(int width, int height, Bitmap.Config config) {
            return null;
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
        BitmapCache bitmapCache = new BitmapCache(mMockResources, new DummyAnimationFactory(), new DummyBitmapDescriptorFactory(), new MockBitmapFactory());
        bitmapCache.loadBitmaps();

        BitmapDescriptor bitmapDescriptor = bitmapCache.getBitmap(1);
        assertEquals(mBitmapDescriptor1, bitmapDescriptor);

        bitmapDescriptor = bitmapCache.getBitmap(2);
        assertEquals(mBitmapDescriptor2, bitmapDescriptor);

        bitmapDescriptor = bitmapCache.getBitmap(3);
        assertEquals(mBitmapDescriptor3, bitmapDescriptor);

    }




}
