package com.game.wargame.Model.Entities.VirtualMap;

import com.game.wargame.Controller.Utils.Location;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by clement on 13/04/16.
 */
public class RealCell extends Cell {

    private LatLng mPosition;
    private ArrayList<LatLng> mVertices;

    public RealCell(CellTypeEnum type, LatLng position, float width, float height) {
        super(type);

        mPosition = position;

        LatLng p1 = Location.getDestinationPoint(position, 0, width / 2);
        LatLng v1 = Location.getDestinationPoint(p1, 90, height / 2);

        LatLng v2 = Location.getDestinationPoint(v1, 0, -width);
        LatLng v3 = Location.getDestinationPoint(v2, -90, height);
        LatLng v4 = Location.getDestinationPoint(v3, 0, width);

        mVertices = new ArrayList<>(4);
        mVertices.add(v1);
        mVertices.add(v2);
        mVertices.add(v3);
        mVertices.add(v4);
    }

    public ArrayList<LatLng> vertices() {
        return mVertices;
    }

    public LatLng position() {
        return mPosition;
    }
}
