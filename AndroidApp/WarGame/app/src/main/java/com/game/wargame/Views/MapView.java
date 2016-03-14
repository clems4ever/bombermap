package com.game.wargame.Views;

import android.support.v4.app.FragmentActivity;

import com.game.wargame.Model.Entities.PlayerModel;
import com.game.wargame.Model.Entities.Projectile;
import com.game.wargame.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class MapView implements OnMapReadyCallback {

    private FragmentActivity mActivity;
    private GoogleMap mMap;
    private com.google.android.gms.maps.MapView mMapView;

    private HashMap<String, Marker> mPlayerLocations;
    private HashMap<String, Marker> mProjectileLocations;

    private OnMapReadyListener mOnMapReadyListener;

    private static final int DEFAULT_ZOOM = 15;

    public MapView(FragmentActivity activity) {
        mActivity = activity;
        mPlayerLocations = new HashMap<>();
        mProjectileLocations = new HashMap<>();

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
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, DEFAULT_ZOOM));
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

    public void renderProjectiles(ArrayList<Projectile> projectiles) {
        for (Projectile projectile : projectiles) {
            renderProjectile(projectile);
        }
    }

    public void addBulletMarker(Projectile projectile)
    {
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(projectile.getPosition())
                    .rotation((float) projectile.getDirection())
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.bullet2)));
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
        mMap.animateCamera(cameraUpdate);
    }

    public void moveCameraTo(LatLng position, float zoom) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(position, zoom);
        mMap.animateCamera(cameraUpdate);
    }


    public interface OnMapReadyListener {
        public void onMapReady();
    }
}
