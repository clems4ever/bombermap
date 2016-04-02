package com.game.wargame.Controller.Utils;

/**
 * Created by sergei on 02/04/16.
 */
public class Location implements IDistanceCalculator{
    public void distanceBetween(double latitude1, double longitude1, double latitude2, double longitude2, float[] results) {
        android.location.Location.distanceBetween(latitude1, longitude1, latitude2, longitude2, results);
    }
}
