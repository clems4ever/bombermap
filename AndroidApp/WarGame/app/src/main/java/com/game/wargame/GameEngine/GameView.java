package com.game.wargame.GameEngine;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Button;

import com.game.wargame.Entities.Player;
import com.game.wargame.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by clement on 10/02/16.
 */
public class GameView implements OnMapReadyCallback {

    private Activity mActivity;
    private GoogleMap mMap;
    private OnMapReadyCallback mOnMapReadyCallback;

    private View.OnClickListener mFireButtonClickedListener;

    private Map<Integer, Marker> mPlayerLocations;

    public GameView(Activity activity) {
        mActivity = activity;
        mPlayerLocations = new HashMap<>();

        Button fireButton = (Button) mActivity.findViewById(R.id.fire_button);

        fireButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mFireButtonClickedListener != null) {
                    mFireButtonClickedListener.onClick(view);
                }
            }
        });
    }

    public void setFireButtonClickedListener(View.OnClickListener onClickListener) {
        mFireButtonClickedListener = onClickListener;
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
        mOnMapReadyCallback.onMapReady(googleMap);
    }

    public void setOnMapReadyListener(OnMapReadyCallback onMapReadyListener) {
        mOnMapReadyCallback = onMapReadyListener;
    }
}
