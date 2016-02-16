package com.game.wargame.GameEngine;

import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.game.wargame.Entities.Player;
import com.game.wargame.R;
import com.game.wargame.WeaponControllers.AbstractWeaponControllerView;
import com.game.wargame.WeaponControllers.DroneControllerView;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Map;


public class GameView implements OnMapReadyCallback, AbstractWeaponControllerView.OnActionFinishedListener {

    private Activity mActivity;
    private GoogleMap mMap;
    private OnMapReadyCallback mOnMapReadyCallback;

    private RelativeLayout mMapLayout;
    private AbstractWeaponControllerView mWeaponControllerInterface;
    private Button mFireButton;

    private OnWeaponTargetDefinedListener mOnWeaponTargetDefined;

    private Map<String, Marker> mPlayerLocations;
    private String mCurrentPlayerId;

    public GameView(final Activity activity) {
        mActivity = activity;
        mPlayerLocations = new HashMap<>();
        final GameView that = this;

        mFireButton = (Button) mActivity.findViewById(R.id.fire_button);

        mFireButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                that.setWeaponController(new DroneControllerView(activity));
            }
        });

        mMapLayout = (RelativeLayout) activity.findViewById(R.id.map_layout);
    }

    public void setOnWeaponTargetDefinedListener(OnWeaponTargetDefinedListener onWeaponTargetDefinedListener) {
        mOnWeaponTargetDefined = onWeaponTargetDefinedListener;
    }

    public void addPlayer(Player player) {

        if(mMap == null) return;

        Marker playerMarker = mMap.addMarker(new MarkerOptions()
                .position(player.getPosition())
                .anchor(0.5f, 0.35f)
                .flat(true)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.marker)));

        mPlayerLocations.put(player.getPlayerId(), playerMarker);
    }

    public void setCurrentPlayerId(String playerId) {
        mCurrentPlayerId = playerId;
    }

    public void movePlayer(Player player) {
        if(mMap == null) return;

        Marker marker = mPlayerLocations.get(player.getPlayerId());

        if(marker != null) {
            marker.setPosition(player.getPosition());
        }
    }

    public void rotatePlayer(Player player) {
        if(mMap == null) return;

        Marker marker = mPlayerLocations.get(player.getPlayerId());

        if(marker != null) {
            marker.setRotation(player.getRotation());
        }
    }

    public void animateCamera(CameraUpdate cameraUpdate) {
        mMap.animateCamera(cameraUpdate);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if(mOnMapReadyCallback != null) {
            mOnMapReadyCallback.onMapReady(googleMap);
        }
    }

    public void setOnMapReadyListener(OnMapReadyCallback onMapReadyListener) {
        mOnMapReadyCallback = onMapReadyListener;
    }

    public void setWeaponController(AbstractWeaponControllerView weaponController) {
        mWeaponControllerInterface = weaponController;

        if(mWeaponControllerInterface != null) {
            mFireButton.setSelected(true);

            mWeaponControllerInterface.initialize();
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);

            mMapLayout.addView(mWeaponControllerInterface, params);
            mWeaponControllerInterface.setOnActionFinishedListener(this);
            mWeaponControllerInterface.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {

                        float targetX = event.getX();
                        float targetY = event.getY();

                        if(mOnWeaponTargetDefined != null) {
                            mOnWeaponTargetDefined.onWeaponTargetDefined(targetX, targetY);
                        }
                        onActionFinished();
                    }
                    return true;
                }
            });
            mMapLayout.invalidate();
        }
    }

    @Override
    public void onActionFinished() {
        if(mWeaponControllerInterface != null) {
            mWeaponControllerInterface.finalize();
            mMapLayout.removeView(mWeaponControllerInterface);
            mMapLayout.invalidate();
            mWeaponControllerInterface = null;
            mFireButton.setSelected(false);
        }
    }

    public Projection getMapProjection() {
        return mMap.getProjection();
    }

    public interface OnWeaponTargetDefinedListener {
        public void onWeaponTargetDefined(float x, float y);
    }
}
