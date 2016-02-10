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

    private static SensorManager mySensorManager;

    private float[] mGData = new float[3];
    private float[] mMData = new float[3];

    private float[] mR = new float[16];
    private float[] mI = new float[16];
    private float[] mOrientation = new float[3];
    private int mCount;

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

        mySensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor gsensor = mySensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor msensor = mySensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        mySensorManager.registerListener(mySensorEventListener, gsensor, SensorManager.SENSOR_DELAY_GAME);
        mySensorManager.registerListener(mySensorEventListener, msensor, SensorManager.SENSOR_DELAY_GAME);
        Toast.makeText(this, "Start ORIENTATION Sensor", Toast.LENGTH_LONG).show();

        mLocationRetriever = new LocationRetriever(this);
        mGameEngine = new GameEngine(this);

        Button fireButton = (Button) this.findViewById(R.id.fire_button);

        fireButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private SensorEventListener mySensorEventListener = new SensorEventListener() {

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            int type = event.sensor.getType();
            float[] data;
            if (type == Sensor.TYPE_ACCELEROMETER) {
                data = mGData;
            } else if (type == Sensor.TYPE_MAGNETIC_FIELD) {
                data = mMData;
            } else {
                // we should not be here.
                return;
            }
            for (int i=0 ; i<3 ; i++)
                data[i] = event.values[i];
            SensorManager.getRotationMatrix(mR, mI, mGData, mMData);
            SensorManager.getOrientation(mR, mOrientation);

            final float rad2deg = (float)(180.0f/Math.PI);
            float yaw = mOrientation[0] * rad2deg;
            updateMarkerRotation(yaw);
        }
    };

    @Override
    protected void onStart() {
        super.onStart();

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
        mLocationRetriever.stop();
        mSocket.disconnect();
        super.onStop();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //mMap.setMyLocationEnabled(true);

        // Add a marker in Sydney and move the camera
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
