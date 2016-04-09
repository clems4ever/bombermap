package com.game.wargame.Views.Animations;

import android.graphics.drawable.Drawable;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by sergei on 14/03/16.
 */
public abstract class Animation {

    private static final int DEFAULT_ANIMATION_FRAMERATE = 100;

    protected List<Integer> mDrawablesId;
    protected Iterator<Integer> mFrameIterator;
    protected int mFrameRateMillis;
    protected int mCounterMillis;
    protected int mCurrentFrame;

    protected Animation() {
        mDrawablesId = new LinkedList<>();
        mFrameRateMillis = DEFAULT_ANIMATION_FRAMERATE;
        mCounterMillis = 0;
    }

    public int first() {
        mFrameIterator = mDrawablesId.iterator();
        if (hasNext())
            mCurrentFrame = mFrameIterator.next();
        return mCurrentFrame;
    }

    public int next() {
        if (!hasNext())
            mFrameIterator = mDrawablesId.iterator();
        mCurrentFrame = mFrameIterator.next();
        return mCurrentFrame;
    }

    public void addTime(int millis) {
        int previousCounter = mCounterMillis;
        mCounterMillis += millis;
        if (mCounterMillis/mFrameRateMillis > previousCounter/mFrameRateMillis)
        {
            next();
        }
    }

    public int current() {
        return mCurrentFrame;
    }

    public boolean hasNext() {
        return mFrameIterator.hasNext();
    }
}
