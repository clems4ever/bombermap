package com.game.wargame.Controller.Utils;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by sergei on 02/04/16.
 */
public class Location implements IDistanceCalculator
{
    public void distanceBetween(double latitude1, double longitude1, double latitude2, double longitude2, float[] results) {
        android.location.Location.distanceBetween(latitude1, longitude1, latitude2, longitude2, results);
    }


    public static LatLng getDestinationPoint(LatLng startLoc, float bearing, float depth)
    {
        LatLng newLocation = null;

        double radius = 6371000.0; // earth's mean radius in km
        double lat1 = Math.toRadians(startLoc.latitude);
        double lng1 = Math.toRadians(startLoc.longitude);
        double brng = Math.toRadians(bearing);
        double lat2 = Math.asin( Math.sin(lat1)*Math.cos(depth/radius) + Math.cos(lat1)*Math.sin(depth/radius)*Math.cos(brng) );
        double lng2 = lng1 + Math.atan2(Math.sin(brng)*Math.sin(depth/radius)*Math.cos(lat1), Math.cos(depth/radius)-Math.sin(lat1)*Math.sin(lat2));
        lng2 = (lng2+Math.PI)%(2*Math.PI) - Math.PI;

        // normalize to -180...+180
        if (lat2 == 0 || lng2 == 0)
        {
            newLocation = new LatLng(0.0f, 0.0f);
        }
        else
        {
            newLocation = new LatLng(Math.toDegrees(lat2), Math.toDegrees(lng2));
        }

        return newLocation;
    };
}
