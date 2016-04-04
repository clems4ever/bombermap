package com.game.wargame.Views;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.app.FragmentActivity;

import com.game.wargame.Model.Entities.EntitiesModel;
import com.game.wargame.Model.Entities.Entity;
import com.game.wargame.Model.Entities.VirtualMap.CellTypeEnum;
import com.game.wargame.Model.Entities.VirtualMap.Map;
import com.game.wargame.Model.Entities.VirtualMap.RealMap;
import com.game.wargame.Model.GameContext.GameContext;
import com.game.wargame.R;
import com.game.wargame.Views.Animations.Animation;
import com.game.wargame.Views.Animations.AnimationFactory;
import com.game.wargame.Views.Animations.BitmapCache;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;

public class MapView implements GoogleMapView.OnMapReadyCallback, EntityDisplayer {

    public static final int LOCAL_PLAYER_MARKER_RES_ID = R.mipmap.marker_current;
    public static final int REMOTE_PLAYER_MARKER_RES_ID = R.mipmap.marker;

    private FragmentActivity mActivity;
    private GoogleMap mGoogleMap;
    private IGoogleMapView mGoogleMapView;

    private HashMap<String, PlayerMarker> mPlayerLocations;
    private HashMap<String, Marker> mEntityMarkers;
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

    public void addEntityMarker(Entity entity) {
        Animation animation = entity.getAnimation();
        Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                .position(entity.getPosition())
                .rotation((float) entity.getDirection())
                .icon(mBitmapCache.getBitmap(animation.current())));
        mEntityMarkers.put(entity.getUUID(), marker);
    }

    public void display(final Entity entity) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Animation animation = entity.getAnimation();
                Marker marker = mEntityMarkers.get(entity.getUUID());
                if (marker == null && !entity.isToRemove()) {
                    addEntityMarker(entity);
                } else if (marker != null) {
                    if (entity.isToRemove()) {
                        marker.remove();
                        mEntityMarkers.remove(entity.getUUID());
                    } else {
                        marker.setPosition(entity.getPosition());
                        marker.setIcon(mBitmapCache.getBitmap(animation.current()));
                    }
                }
            }
        });
    }

    public void display(EntitiesModel entitiesModel) {
        ArrayList<Entity> entities = entitiesModel.getEntities();
        for (Entity entity : entities) {
            display(entity);
            if (entity.isToRemove())
                //The View should not update the model directly
                entitiesModel.removeEntity(entity);
        }
    }

    public void display(GameContext gameContext) {

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

    public void updateVirtualMapOverlay(RealMap virtualMap) {
        Bitmap block = BitmapFactory.decodeResource(mActivity.getResources(), R.mipmap.wall);
        Bitmap scaledBlock = Bitmap.createScaledBitmap(block, 32, 32, false);

        int blockWidth = scaledBlock.getWidth();
        int blockHeight = scaledBlock.getHeight();

        int imageWidth = blockWidth * virtualMap.getMap().width();
        int imageHeight = blockHeight * virtualMap.getMap().height();

        Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
        Bitmap groundImage = Bitmap.createBitmap(imageWidth, imageHeight, conf);
        Canvas canvas = new Canvas(groundImage);
        Paint p = new Paint();

        for(int i=0; i< virtualMap.getMap().width(); ++i)
        {
            for(int j=0; j< virtualMap.getMap().height(); ++j) {

                if(virtualMap.getMap().cell(i, j).type() == CellTypeEnum.BLOCK) {
                    canvas.drawBitmap(scaledBlock, i * blockWidth, j * blockHeight, p);
                }
            }
        }

        BitmapDescriptor groundBitmapDescriptor = mBitmapDescriptorFactory.fromBitmap(groundImage);
        LatLng blockPosition = virtualMap.getRealCenter();

        mGoogleMap.addBlock(new GroundOverlayOptions()
                .position(blockPosition, virtualMap.getRealWidth(), virtualMap.getRealHeight())
                .anchor(0.5f, 0.5f)
                .zIndex(-100)
                .bearing(virtualMap.getRealRotation())
                .image(groundBitmapDescriptor));
    }

    public interface OnMapReadyListener {
        public void onMapReady();
    }
}
