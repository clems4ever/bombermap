package com.game.wargame.Views;

import android.support.v4.app.FragmentActivity;

import com.game.wargame.Model.Entities.PlayerModel;
import com.game.wargame.R;
import com.game.wargame.Views.Animation.AnimationTimer;
import com.game.wargame.Views.Animation.BulletAnimation;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
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
    private com.google.android.gms.maps.MapView mMapView;

    private Map<String, Marker> mPlayerLocations;
    private AnimationTimer mAnimationTimer;

    private OnMapReadyListener mOnMapReadyListener;

    public MapView(FragmentActivity activity) {
        mActivity = activity;
        mPlayerLocations = new HashMap<>();
        mAnimationTimer = new AnimationTimer(mActivity);

        mMapView = (com.google.android.gms.maps.MapView) mActivity.findViewById(R.id.map);
        mMapView.onCreate(null);
        mMapView.getMapAsync(this);
    }

    public void load(OnMapReadyListener onMapReadyListener) {
        mOnMapReadyListener = onMapReadyListener;
    }

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        UiSettings uiSettings = mMap.getUiSettings();

        uiSettings.setZoomControlsEnabled(true);
        mAnimationTimer.start();

        if(mOnMapReadyListener != null) {
            mOnMapReadyListener.onMapReady();
        }
        mMapView.onResume();
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

    public void removePlayer(final PlayerModel player) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Marker marker = mPlayerLocations.get(player.getPlayerId());

                if(marker != null) {
                    marker.remove();
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

    public interface OnMapReadyListener {
        public void onMapReady();
    }
}
