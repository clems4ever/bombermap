package com.game.wargame;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.Toast;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.net.URISyntaxException;
import java.util.List;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private Compass mCompass;

    private GoogleMap mMap;
    private LocationRetriever mLocationRetriever;
    private GameEngine mGameEngine;
    private Marker mMarker;
    private Circle mCircle;

    private Socket mSocket;

    {
        try {
            mSocket = IO.socket("http://chat.socket.io");
        } catch (URISyntaxException e) {
            Log.d("TEST", "Error");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mLocationRetriever = new LocationRetriever(this);
        mGameEngine = new GameEngine(this);
        mCompass = new Compass(this);

        Button fireButton = (Button) this.findViewById(R.id.fire_button);

        fireButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        mCompass.start(new Compass.OnCompassChangedListener() {
            @Override
            public void onCompassChanged(float yaw, float roll, float pitch) {
                updateMarkerRotation(yaw);
            }
        });

        mSocket.on("data_down", new OnPlayerFeedListener(this));
        mSocket.connect();

        mLocationRetriever.start(new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                String position = String.valueOf(location.getLatitude()) + ", " + String.valueOf(location.getLongitude());
                mSocket.emit("data_up", position);

                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(latLng);
                mMap.animateCamera(cameraUpdate);

                mCircle.setCenter(latLng);
                mMarker.setPosition(latLng);
                mMarker.setRotation(90);
            }
        });
    }

    protected void updateMarkerRotation(float yaw) {
        if(mMarker != null) {
            mMarker.setRotation(yaw);
        }
    }

    @Override
    protected void onStop() {
        mCompass.stop();
        mLocationRetriever.stop();
        mSocket.disconnect();
        super.onStop();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng lafourche = new LatLng(48.8867726, 2.3289736);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(lafourche));

        mCircle = mMap.addCircle(new CircleOptions()
                .center(lafourche)
                .radius(200)
                .strokeColor(Color.RED));

        mMarker = mMap.addMarker(new MarkerOptions()
                .position(lafourche)
                .anchor(0.5f, 0.35f)
                .flat(true)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.marker)));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lafourche, 15));
    }

}
