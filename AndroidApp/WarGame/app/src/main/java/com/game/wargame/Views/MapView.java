package com.game.wargame.Views;

import android.support.v4.app.FragmentActivity;

import com.game.wargame.Model.Entities.PlayerModel;
import com.game.wargame.Model.Entities.Projectile;
import com.game.wargame.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;

public class MapView implements GoogleMapViewWrapper.OnMapReadyCallback {

    public static final int LOCAL_PLAYER_MARKER_RES_ID = R.mipmap.marker_current;
    public static final int REMOTE_PLAYER_MARKER_RES_ID = R.mipmap.marker;
    public static final int BULLET_MARKER_RES_ID = R.mipmap.bullet2;

    private FragmentActivity mActivity;
    private GoogleMapWrapper mGoogleMap;
    private GoogleMapViewWrapper mGoogleMapViewWrapper;

    private HashMap<String, Marker> mPlayerLocations;
    private HashMap<String, Marker> mProjectileLocations;

    private BitmapDescriptorFactory mBitmapDescriptorFactory;
    private OnMapReadyListener mOnMapReadyListener;

    private static final int DEFAULT_ZOOM = 15;


    public MapView(FragmentActivity fragmentActivity, GoogleMapViewWrapper googleMapViewWrapper, com.game.wargame.Views.BitmapDescriptorFactory bitmapDescriptorFactory) {
        init(fragmentActivity, googleMapViewWrapper, bitmapDescriptorFactory);
    }

    private void init(FragmentActivity activity, GoogleMapViewWrapper googleMapViewWrapper, com.game.wargame.Views.BitmapDescriptorFactory bitmapDescriptorFactory) {
        mActivity = activity;
        mBitmapDescriptorFactory = bitmapDescriptorFactory;
        mPlayerLocations = new HashMap<>();
        mProjectileLocations = new HashMap<>();

        mGoogleMapViewWrapper = googleMapViewWrapper;
        googleMapViewWrapper.onCreate(null);
    }

    public void startAsync(OnMapReadyListener onMapReadyListener) {
        mOnMapReadyListener = onMapReadyListener;
        mGoogleMapViewWrapper.getMapAsync(this);
    }

    public void onMapReady(GoogleMapWrapper googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setZoomControlEnabled(true);

        if(mOnMapReadyListener != null) {
            mOnMapReadyListener.onMapReady();
        }
        mGoogleMapViewWrapper.onResume();
    }

    public Projection getMapProjection() {
        return mGoogleMap.getProjection();
    }

    public void addLocalPlayer(final String playerId) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                BitmapDescriptor bmp = mBitmapDescriptorFactory.fromResource(LOCAL_PLAYER_MARKER_RES_ID);
                Marker playerMarker = mGoogleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(0, 0))
                        .anchor(0.5f, 0.35f)
                        .flat(true)
                        .icon(bmp));

                mPlayerLocations.put(playerId, playerMarker);
            }
        });
    }

    public void addRemotePlayer(final String playerId) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                BitmapDescriptor bmp = mBitmapDescriptorFactory.fromResource(REMOTE_PLAYER_MARKER_RES_ID);
                Marker playerMarker = mGoogleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(0, 0))
                        .anchor(0.5f, 0.35f)
                        .flat(true)
                        .icon(bmp));

                mPlayerLocations.put(playerId, playerMarker);
            }
        });
    }

    public void movePlayerTo(final String playerId, final LatLng position) {
        if(mGoogleMap == null) return;

        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Marker marker = mPlayerLocations.get(playerId);
                if (marker != null) {
                    marker.setPosition(position);
                }
            }
        });
    }

    public void renderProjectiles(ArrayList<Projectile> projectiles) {
        for (Projectile projectile : projectiles) {
            renderProjectile(projectile);
        }
    }

    public void addBulletMarker(Projectile projectile)
    {
            Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                    .position(projectile.getPosition())
                    .rotation((float) projectile.getDirection())
                    .icon(mBitmapDescriptorFactory.fromResource(BULLET_MARKER_RES_ID)));
            mProjectileLocations.put(projectile.getUUID(), marker);
    }

    public void renderProjectile(final Projectile projectile) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Marker marker = mProjectileLocations.get(projectile.getUUID());
                if (marker == null) {
                    addBulletMarker(projectile);
                } else {
                    if(projectile.isToDestroy()) {
                        marker.remove();
                        mProjectileLocations.remove(projectile.getUUID());
                    } else {
                        marker.setPosition(projectile.getPosition());
                    }
                }
            }
        });
    }

    public void removePlayer(final PlayerModel player) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Marker marker = mPlayerLocations.get(player.getPlayerId());

                if (marker != null) {
                    marker.remove();
                }

            }
        });
    }

    public void moveCameraTo(LatLng position) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(position);
        mGoogleMap.animateCamera(cameraUpdate);
    }

    public void moveCameraTo(LatLng position, float zoom) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(position, zoom);
        mGoogleMap.animateCamera(cameraUpdate);
    }


    public interface OnMapReadyListener {
        public void onMapReady();
    }
}
