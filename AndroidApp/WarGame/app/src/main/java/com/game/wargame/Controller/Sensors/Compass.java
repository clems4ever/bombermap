package com.game.wargame.Controller.Sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by clement on 10/02/16.
 */
public class Compass {

    private Context mContext;

    private static SensorManager mySensorManager;

    private float[] mGData = new float[3];
    private float[] mMData = new float[3];

    private float[] mR = new float[16];
    private float[] mI = new float[16];
    private float[] mOrientation = new float[3];

    private OnCompassChangedListener mOnCompassChangedListener;

    public Compass(Context context) {
        mContext = context;
    }

    public void start(OnCompassChangedListener onCompassChangedListener) {
        mOnCompassChangedListener = onCompassChangedListener;

        mySensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        Sensor gsensor = mySensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor msensor = mySensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        mySensorManager.registerListener(mySensorEventListener, gsensor, SensorManager.SENSOR_DELAY_GAME);
        mySensorManager.registerListener(mySensorEventListener, msensor, SensorManager.SENSOR_DELAY_GAME);
    }

    public void stop() {
        mySensorManager.unregisterListener(mySensorEventListener);
        mOnCompassChangedListener = null;
    }

    private SensorEventListener mySensorEventListener = new SensorEventListener() {

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            if(mOnCompassChangedListener == null) return;

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
            float roll = mOrientation[1] * rad2deg;
            float pitch = mOrientation[2] * rad2deg;

            mOnCompassChangedListener.onCompassChanged(yaw, roll, pitch);
        }
    };

    public interface OnCompassChangedListener {
        void onCompassChanged(float yaw, float roll, float pitch);
    }
}
