package com.game.wargame.Views;

import com.game.wargame.Views.VirtualMap.Block;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by developer on 3/13/16.
 */
public class GoogleMap {

    private com.google.android.gms.maps.GoogleMap mGoogleMap;


    public GoogleMap(com.google.android.gms.maps.GoogleMap googleMap) {
        mGoogleMap = googleMap;
    }

    public void setGoogleMap(com.google.android.gms.maps.GoogleMap googleMap) {
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

    public PlayerMarker addPlayerMarker(GroundOverlayOptions options) {
        return new PlayerMarker(mGoogleMap.addGroundOverlay(options));
    }

    public Block addBlock(GroundOverlayOptions options) {
        return new Block(mGoogleMap.addGroundOverlay(options));
    }

    public void animateCamera(CameraUpdate cameraUpdate) {
        mGoogleMap.animateCamera(cameraUpdate);
    }

    public void setOnMapClickListener(com.google.android.gms.maps.GoogleMap.OnMapClickListener onMapClickListener) {
        mGoogleMap.setOnMapClickListener(onMapClickListener);
    }

    public void setOnMapLongClickListener(com.google.android.gms.maps.GoogleMap.OnMapLongClickListener onMapLongClickListener) {
        mGoogleMap.setOnMapLongClickListener(onMapLongClickListener);
    }

}
