package com.game.wargame.Model.Entities.VirtualMap;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by clement on 03/04/16.
 */
public class RealMap {

    private Map mMap;
    private LatLng mRealCenter;
    private float mRealWidth; // in meters
    private float mRealHeight; // in meters

    private float mRealRotation;

    public RealMap(Map map, LatLng center, float realWidth, float realHeight, float realRotation) {
        mMap = map;
        mRealCenter = center;
        mRealHeight = realHeight;
        mRealWidth = realWidth;
        mRealRotation = realRotation;
    }

    public LatLng getRealCenter() {
        return mRealCenter;
    }

    public float getRealWidth() {
        return mRealWidth;
    }

    public float getRealHeight() {
        return mRealHeight;
    }

    public float getRealRotation() {
        return mRealRotation;
    }

    public Map getMap() {
        return mMap;
    }

}
