package com.game.wargame.Controller.GameLogic;

import android.location.Location;

import com.game.wargame.Model.Entities.EntitiesModel;
import com.game.wargame.Model.Entities.Entity;
import com.game.wargame.Model.Entities.Player;
import com.game.wargame.Model.Entities.PlayerModel;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by sergei on 24/02/16.
 */
public class CollisionManager {

    public void treatPlayerEntitiesCollisions(EntitiesModel entitiesModel, PlayerModel player, double time)
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
        Location.distanceBetween(playerPosition.latitude, playerPosition.longitude, entityPosition.latitude, entityPosition.longitude, results);

        return results[0] < e.getRadius();
    }

}
