package com.game.wargame.Views.Animations;

import android.app.Activity;

import com.game.wargame.Controller.Engine.DisplayTransaction;
import com.game.wargame.Views.GoogleMap.GoogleMap;
import com.game.wargame.Views.GoogleMap.GoogleMapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.sql.Time;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;

/**
 * Created by clement on 16/04/16.
 */
public class GoogleMapAnimation {

    protected BlockingQueue<Integer> mDrawablesId;
    private Timer mTimer;
    private int mFrameRate = 100;
    private LatLng mPosition;
    private float mDirection;

    private BitmapCache mBitmapCache;
    private Marker mMarker;

    public GoogleMapAnimation(LatLng position, float direction, BitmapCache bitmapCache) {
        mTimer = new Timer();

        mPosition = position;
        mDirection = direction;
        mBitmapCache = bitmapCache;
    }

    public void start(final GoogleMap googleMap) {

        if(mDrawablesId.size() == 0) {
            return;
        }

        try {
            Integer drawableId = mDrawablesId.take();
            mMarker = googleMap.addMarker(new MarkerOptions()
                    .position(mPosition)
                    .rotation(mDirection)
                    .icon(mBitmapCache.getBitmap(drawableId)));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    if(mDrawablesId.size() == 0) {
                        mTimer.cancel();
                    }
                    else {
                        Integer drawableId = mDrawablesId.take();
                        mMarker.setIcon(mBitmapCache.getBitmap(drawableId));
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, mFrameRate, mFrameRate);
    }
}
