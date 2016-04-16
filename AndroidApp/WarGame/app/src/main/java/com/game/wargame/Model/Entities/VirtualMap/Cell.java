package com.game.wargame.Model.Entities.VirtualMap;

import com.game.wargame.Controller.Utils.Location;
import com.game.wargame.Model.Entities.EntitiesContainer;
import com.game.wargame.Model.Entities.Entity;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by developer on 3/20/16.
 */
public class Cell {

    private CellTypeEnum m_type;

    public Cell(CellTypeEnum type) {
        m_type = type;
    }

    public CellTypeEnum type() {
        return m_type;
    }



}
