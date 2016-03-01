package com.game.wargame.Model.Entities;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by sergei on 01/03/16.
 */
public class Tile {

    public LatLng getPosition() {
        return mPosition;
    }

    public void setPosition(LatLng position) {
        mPosition = position;
    }

    protected LatLng mPosition;
}
