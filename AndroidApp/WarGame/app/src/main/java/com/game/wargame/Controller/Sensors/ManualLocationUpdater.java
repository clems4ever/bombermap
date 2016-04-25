package com.game.wargame.Controller.Sensors;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by clement on 24/04/16.
 */
public class ManualLocationUpdater extends AbstractLocationRetriever implements GoogleMap.OnMapLongClickListener {

    public ManualLocationUpdater() {
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        if(mOnLocationRetrievedListener != null) {
            mOnLocationRetrievedListener.onLocationRetrieved(latLng.latitude, latLng.longitude);
        }
    }
}
