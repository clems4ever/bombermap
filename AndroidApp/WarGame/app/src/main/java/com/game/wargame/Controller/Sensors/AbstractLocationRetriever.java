package com.game.wargame.Controller.Sensors;

/**
 * Created by clement on 09/04/16.
 */
public abstract class AbstractLocationRetriever {

    protected OnLocationRetrievedListener mOnLocationRetrievedListener;

    public AbstractLocationRetriever() {
    }

    public void setOnLocationRetrievedListener(OnLocationRetrievedListener onLocationRetrievedListener) {
        mOnLocationRetrievedListener = onLocationRetrievedListener;
    }

    public abstract void start();
    public abstract void stop();

}
