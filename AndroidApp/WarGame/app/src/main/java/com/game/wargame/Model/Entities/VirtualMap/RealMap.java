package com.game.wargame.Model.Entities.VirtualMap;

import com.game.wargame.Controller.Utils.Location;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by clement on 03/04/16.
 */
public class RealMap {

    private Map<RealCell> mCells;
    private LatLng mRealCenter;
    private float mCellWidth;
    private float mCellHeight;

    private float mMapWidth;
    private float mMapHeight;

    private float mRealRotation;

    public RealMap(Map<Cell> map, LatLng center, float cellWidth, float cellHeight, float realRotation) {
        mRealCenter = center;
        mCellHeight = cellHeight;
        mCellWidth = cellWidth;
        mRealRotation = realRotation;

        mCells = new Map<>(map.width(), map.height(), null);

        mMapWidth = map.width() * mCellWidth;
        mMapHeight = map.height() * mCellHeight;

        for(int x=0; x< map.width(); ++x) {
            for(int y=0; y< map.height(); ++y) {
                LatLng xv = Location.getDestinationPoint(mRealCenter, mRealRotation + 90, x * mCellWidth - mMapWidth / 2);
                LatLng yv = Location.getDestinationPoint(xv, mRealRotation + 180, y * mCellHeight - mMapHeight / 2);

                RealCell cell = new RealCell(map.get(x, y).type(), yv, mCellWidth, mCellHeight);
                mCells.set(x, y, cell);
            }
        }

    }

    public LatLng center() {
        return mRealCenter;
    }

    public int width() { return mCells.width(); }
    public int height() { return mCells.height(); }

    public float mapWidth() {
        return mMapWidth;
    }
    public float mapHeight() {
        return mMapHeight;
    }
    public float cellWidth() { return mCellWidth; }
    public float cellHeight() { return mCellHeight; }

    public float getRealRotation() {
        return mRealRotation;
    }

    public RealCell getRealCell(int x, int y) {
        return mCells.get(x, y);
    }
}
