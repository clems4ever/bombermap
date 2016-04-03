package com.game.wargame.Views.VirtualMap;

import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;

/**
 * Created by clement on 03/04/16.
 */
public class Block {

    private GroundOverlay mGroundOverlay;

    public Block(GroundOverlay groundOverlay) {
        mGroundOverlay = groundOverlay;
    }

    public void remove() {
        mGroundOverlay.remove();
    }

}
