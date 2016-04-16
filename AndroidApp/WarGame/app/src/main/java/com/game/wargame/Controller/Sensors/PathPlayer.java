package com.game.wargame.Controller.Sensors;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by clement on 09/04/16.
 */
public class PathPlayer extends AbstractLocationRetriever {

    private static int TIMER_TIME = 500;

    BlockingQueue<LatLng> mPath;
    Timer mTimer;

    boolean mInfiniteLoop;

    public PathPlayer(JSONArray path, boolean reverse, boolean infiniteLoop) {

        mPath = new LinkedBlockingQueue<>();
        mTimer = new Timer();

        mInfiniteLoop = infiniteLoop;

        buildPathQueue(path, reverse);
    }

    private void buildPathQueue(JSONArray pathArray, boolean reverse) {
        try {
            int positionCount = pathArray.length();

            for (int i = 0; i < positionCount; ++i) {
                JSONObject position = pathArray.getJSONObject(i);

                LatLng p = new LatLng(position.getDouble("lat"), position.getDouble("long"));

                mPath.put(p);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void triggerPositionUpdate() {
        try {
            LatLng position = mPath.take();

            if(mInfiniteLoop) {
                mPath.put(position);
            }

            if(mOnLocationRetrievedListener != null) {
                mOnLocationRetrievedListener.onLocationRetrieved(position.latitude, position.longitude);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // If not empty, start next task
        if(!mPath.isEmpty()) {
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    triggerPositionUpdate();
                }
            }, TIMER_TIME);
        }
    }


    public void start()
    {
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                triggerPositionUpdate();
            }
        }, TIMER_TIME);
    }

    public void stop()
    {
        mTimer.cancel();
    }

}
