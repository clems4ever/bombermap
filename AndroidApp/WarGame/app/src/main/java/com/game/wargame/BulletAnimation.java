package com.game.wargame;

import android.location.Location;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class BulletAnimation {

    private Queue<LatLng> mPositionsQueue = new LinkedBlockingQueue<>();

    private LatLng mStart;
    private LatLng mEnd;
    private double mSpeed; // in meters / second
    private GoogleMap mGoogleMap;

    private Marker mMarker;
    private LatLng mCurrentPosition;
    private float mOrientation;

    public BulletAnimation(LatLng start, LatLng end, double speed) {
        mStart = start;
        mEnd = end;
        mSpeed = speed;

        mCurrentPosition = mStart;

        computeTrajectory();
    }

    private void computeTrajectory() {
        double deltaLatitude = mEnd.latitude - mStart.latitude;
        double deltaLongitude = mEnd.longitude - mStart.longitude;

        float[] results = new float[1];
        Location.distanceBetween(mStart.latitude, mStart.longitude, mEnd.latitude, mEnd.longitude, results);

        double distance = results[0]; // in meters

        double time = distance / mSpeed * 1000.0f;
        int occurrences = (int) (time / 200.0f);

        double dLatitude = deltaLatitude / occurrences;
        double dLongitude = deltaLongitude / occurrences;

        for(int i=0; i<occurrences; ++i) {
            LatLng position = new LatLng(mStart.latitude + i * dLatitude, mStart.longitude + i * dLongitude);
            mPositionsQueue.add(position);
        }

        mOrientation = (float) SphericalUtil.computeHeading(mStart, mEnd) + 90;
    }

    public void setGoogleMap(GoogleMap googleMap) {
        mGoogleMap = googleMap;
    }

    public boolean hasNext() {
        return mPositionsQueue.size() > 0;
    }

    public LatLng next() {
        mCurrentPosition = mPositionsQueue.poll();
        return mCurrentPosition;
    }

    public void onStart() {
        mMarker = mGoogleMap.addMarker(new MarkerOptions()
                .position(mStart)
                .rotation(mOrientation)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.bullet2)));
    }

    public void onDraw() {
        mMarker.setPosition(mCurrentPosition);
    }

    public void onEnd() {
        mMarker.remove();
    }
}
