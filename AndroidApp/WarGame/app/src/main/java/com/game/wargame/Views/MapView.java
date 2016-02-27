package com.game.wargame.Views;

import android.graphics.Bitmap;
import android.support.v4.app.FragmentActivity;

import com.game.wargame.Views.Animation.AnimationTimer;
import com.game.wargame.Views.Animation.BulletAnimation;
import com.game.wargame.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Map;

public class MapView implements OnMapReadyCallback {

    private FragmentActivity mActivity;
    private GoogleMap mMap;

    private Map<String, Marker> mPlayerLocations;
    private AnimationTimer mAnimationTimer;

    public MapView(FragmentActivity activity) {
        mActivity = activity;
        mPlayerLocations = new HashMap<>();
        mAnimationTimer = new AnimationTimer(mActivity);

        SupportMapFragment mapFragment = (SupportMapFragment) mActivity.getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        UiSettings uiSettings = mMap.getUiSettings();

        uiSettings.setZoomControlsEnabled(true);
        mAnimationTimer.start();
    }

    public Projection getMapProjection() {
        return mMap.getProjection();
    }

    public void movePlayerTo(final String playerId, final boolean currentPlayer, final LatLng position) {
        if(mMap == null) return;

        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Marker marker = mPlayerLocations.get(playerId);
                if (marker != null) {
                    marker.setPosition(position);
                } else {
                    BitmapDescriptor bmp = null;
                    if(currentPlayer) {
                        bmp = BitmapDescriptorFactory.fromResource(R.mipmap.marker_current);
                    }
                    else {
                        bmp = BitmapDescriptorFactory.fromResource(R.mipmap.marker);
                    }

                    Marker playerMarker = mMap.addMarker(new MarkerOptions()
                            .position(position)
                            .anchor(0.5f, 0.35f)
                            .flat(true)
                            .icon(bmp));
                    mPlayerLocations.put(playerId, playerMarker);
                }
            }
        });
    }

    public void moveCameraTo(LatLng position) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(position);
        mMap.animateCamera(cameraUpdate);
    }

    public void moveCameraTo(LatLng position, float zoom) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(position, zoom);
        mMap.animateCamera(cameraUpdate);
    }

    public void startAnimation(final BulletAnimation bulletAnimation) {
        bulletAnimation.setGoogleMap(mMap);
        mAnimationTimer.startBulletAnimation(bulletAnimation);
    }

}
