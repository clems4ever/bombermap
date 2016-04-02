package com.game.wargame.Controller.GameLogic;

import com.game.wargame.Controller.Utils.IDistanceCalculator;
import com.game.wargame.Controller.Utils.Location;

import com.game.wargame.Model.Entities.EntitiesModel;
import com.game.wargame.Model.Entities.Entity;
import com.game.wargame.Model.Entities.LocalPlayerModel;
import com.game.wargame.Model.Entities.Player;
import com.game.wargame.Model.Entities.PlayerModel;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by sergei on 24/02/16.
 */
public class CollisionManager {

    IDistanceCalculator mDistanceCalculator;

    public CollisionManager(IDistanceCalculator distanceCalculator) {
        mDistanceCalculator = distanceCalculator;
    }

    public void treatPlayerEntitiesCollisions(EntitiesModel entitiesModel, LocalPlayerModel player, double time)
    {
        ArrayList<Entity> entities = entitiesModel.getEntities();
        for (Entity entity : entities) {
            if (areColliding(entity, player))
            {
                entity.onCollision(player, time);
            }
        }
    }

    private boolean areColliding(Entity e, PlayerModel p)
    {
        float[] results = new float[1];
        LatLng entityPosition = e.getPosition();
        LatLng playerPosition = p.getPosition();
        mDistanceCalculator.distanceBetween(playerPosition.latitude, playerPosition.longitude, entityPosition.latitude, entityPosition.longitude, results);

        return results[0] < e.getRadius();
    }

}
