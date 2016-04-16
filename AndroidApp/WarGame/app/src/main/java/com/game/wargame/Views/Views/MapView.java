package com.game.wargame.Views.Views;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.FragmentActivity;

import com.game.wargame.Model.Entities.Entity;
import com.game.wargame.Model.Entities.Players.Player;
import com.game.wargame.Model.Entities.VirtualMap.CellTypeEnum;
import com.game.wargame.Model.Entities.VirtualMap.RealCell;
import com.game.wargame.Model.Entities.VirtualMap.RealMap;
import com.game.wargame.Model.GameContext.GameContext;
import com.game.wargame.R;
import com.game.wargame.Views.Animations.Animation;
import com.game.wargame.Views.Animations.AnimationFactory;
import com.game.wargame.Views.Animations.BitmapCache;
import com.game.wargame.Views.BitmapDescriptorFactory;
import com.game.wargame.Views.GoogleMap.GoogleMap;
import com.game.wargame.Views.GoogleMap.GoogleMapView;
import com.game.wargame.Views.GoogleMap.IGoogleMapView;
import com.game.wargame.Views.PlayerMarker;
import com.game.wargame.Views.PlayerMarkerFactory;
import com.game.wargame.Views.VirtualMap.Block;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Iterator;

public class MapView implements GoogleMapView.OnMapReadyCallback {

    public static final int LOCAL_PLAYER_MARKER_RES_ID = R.mipmap.profile_s;
    public static final int REMOTE_PLAYER_MARKER_RES_ID = R.mipmap.profile_c;

    private FragmentActivity mActivity;
    private GoogleMap mGoogleMap;
    private IGoogleMapView mGoogleMapView;

    private HashMap<String, PlayerMarker> mPlayerLocations;
    private HashMap<String, Marker> mEntityMarkers;
    private HashMap<String, Block> mBlockMarkers;
    private BitmapCache mBitmapCache;

    private PlayerMarkerFactory mPlayerMarkerFactory;
    private BitmapDescriptorFactory mBitmapDescriptorFactory;
    private OnMapReadyListener mOnMapReadyListener;


    public MapView(FragmentActivity fragmentActivity, IGoogleMapView googleMapView, com.game.wargame.Views.BitmapDescriptorFactory bitmapDescriptorFactory) {
        init(fragmentActivity, googleMapView, bitmapDescriptorFactory, null);
        mPlayerMarkerFactory = new PlayerMarkerFactory(mBitmapDescriptorFactory);
    }


    // For test
    public MapView(FragmentActivity fragmentActivity, IGoogleMapView googleMapView, com.game.wargame.Views.BitmapDescriptorFactory bitmapDescriptorFactory, PlayerMarkerFactory playerMarkerFactory) {
        init(fragmentActivity, googleMapView, bitmapDescriptorFactory, playerMarkerFactory);
    }

    private void init(FragmentActivity activity, IGoogleMapView googleMapView, com.game.wargame.Views.BitmapDescriptorFactory bitmapDescriptorFactory, PlayerMarkerFactory playerMarkerFactory) {
        mActivity = activity;
        mBitmapDescriptorFactory = bitmapDescriptorFactory;
        mPlayerLocations = new HashMap<>();
        mBlockMarkers = new HashMap<>();

        mPlayerMarkerFactory = playerMarkerFactory;
        mEntityMarkers = new HashMap<>();

        mGoogleMapView = googleMapView;
        mGoogleMapView.onCreate(null);
        mBitmapCache = new BitmapCache(new AnimationFactory(), bitmapDescriptorFactory);
        mBitmapCache.loadBitmaps();
    }

    public void startAsync(OnMapReadyListener onMapReadyListener) {
        mOnMapReadyListener = onMapReadyListener;
        mGoogleMapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setZoomControlEnabled(true);
        mPlayerMarkerFactory.setGoogleMap(mGoogleMap);

        if(mOnMapReadyListener != null) {
            mOnMapReadyListener.onMapReady();
        }
        mGoogleMapView.onResume();
    }

    public Projection getMapProjection() {
        return mGoogleMap.getProjection();
    }

    public void addLocalPlayer(final String playerId) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                PlayerMarker playerMarker = mPlayerMarkerFactory.create(LOCAL_PLAYER_MARKER_RES_ID);
                mPlayerLocations.put(playerId, playerMarker);
            }
        });
    }

    public void addRemotePlayer(final String playerId) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                PlayerMarker playerMarker = mPlayerMarkerFactory.create(REMOTE_PLAYER_MARKER_RES_ID);
                mPlayerLocations.put(playerId, playerMarker);
            }
        });
    }

    public void movePlayerTo(final String playerId, final LatLng position) {
        if (mGoogleMap == null) return;

        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                PlayerMarker marker = mPlayerLocations.get(playerId);
                if (marker != null) {
                    marker.move(position);
                }
            }
        });
    }

    public void display(final Player player) {
        final String playerId = player.getPlayerId();
        final Animation playerAnimation = player.getAnimation();
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                PlayerMarker marker = mPlayerLocations.get(playerId);
                if (marker != null && playerAnimation != null && playerAnimation.isDirty()) {
                    marker.setIcon(mBitmapCache.getBitmap(playerAnimation.current()));
                    playerAnimation.clean();
                }
            }
        });
    }

    public void addEntity(Entity e) {
        Animation animation = e.getAnimation();
        Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                .position(e.getPosition())
                .rotation((float) e.getDirection())
                .anchor(0.5f, 0.5f)
                .icon(mBitmapCache.getBitmap(animation.current())));
        mEntityMarkers.put(e.getUUID(), marker);
    }

    public void updateEntity(Entity e) {
        Marker marker = mEntityMarkers.get(e.getUUID());
        marker.setPosition(e.getPosition());
        Animation animation = e.getAnimation();
        marker.setIcon(mBitmapCache.getBitmap(animation.current()));
        animation.clean();
    }

    public void removeEntity(Entity e) {
        Marker marker = mEntityMarkers.get(e.getUUID());
        marker.remove();
        mEntityMarkers.remove(e.getUUID());
    }

    public void display(GameContext gameContext) {

    }

    public void addBlock(RealCell realCell, float rotation) {

        Bitmap block = BitmapFactory.decodeResource(mActivity.getResources(), R.mipmap.wall);
        BitmapDescriptor scaledBlockDescriptor = mBitmapDescriptorFactory.fromBitmap(block);

        if (realCell.cell().type() == CellTypeEnum.BLOCK) {

            Block b = mGoogleMap.addBlock(new GroundOverlayOptions()
                    .position(realCell.position(), 50, 50)
                    .anchor(0.5f, 0.5f)
                    .zIndex(-100)
                    .bearing(rotation)
                    .image(scaledBlockDescriptor));

            mBlockMarkers.put(realCell.getUUID(), b);

            /*Iterator<LatLng> vIt = realCell.vertices().iterator();
            while(vIt.hasNext()) {
                LatLng p = vIt.next();
                mGoogleMap.addBlock(new GroundOverlayOptions().position(p, 10, 10).image(scaledBlockDescriptor));
            }*/
        }
    }

    public void removeBlock(RealCell realCell) {
        Block b = mBlockMarkers.get(realCell.getUUID());
        if(b != null) {
            b.remove();
            mBlockMarkers.remove(realCell.getUUID());
        }
    }

    public void removePlayer(final String playerId) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                PlayerMarker marker = mPlayerLocations.get(playerId);
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

    public void setOnMapClickListener(com.google.android.gms.maps.GoogleMap.OnMapClickListener onMapClickListener) {
        mGoogleMap.setOnMapClickListener(onMapClickListener);
    }

    public void setOnMapLongClickListener(com.google.android.gms.maps.GoogleMap.OnMapLongClickListener onMapLongClickListener) {
        mGoogleMap.setOnMapLongClickListener(onMapLongClickListener);
    }

    public interface OnMapReadyListener {
        public void onMapReady();
    }
}
