package com.game.wargame.Views;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by developer on 3/13/16.
 */
public class GoogleMapWrapper {

    private GoogleMap mGoogleMap;


    public GoogleMapWrapper(GoogleMap googleMap) {
        mGoogleMap = googleMap;
    }

    public GoogleMapWrapper() {
    }

    public void setGoogleMap(GoogleMap googleMap) {
        mGoogleMap = googleMap;
    }

    public void setZoomControlEnabled(boolean enabled) {
        mGoogleMap.getUiSettings().setZoomControlsEnabled(enabled);
    }

    public Projection getProjection() {
        return mGoogleMap.getProjection();
    }

    public Marker addMarker(MarkerOptions options) {
        return mGoogleMap.addMarker(options);
    }

    public GroundOverlay addGroundOverlay(GroundOverlayOptions options) {
        return mGoogleMap.addGroundOverlay(options);
    }

    public void animateCamera(CameraUpdate cameraUpdate) {
        mGoogleMap.animateCamera(cameraUpdate);
    }

}
