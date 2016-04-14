package com.game.wargame.Controller.GameLogic;

import com.game.wargame.Controller.Utils.IDistanceCalculator;
import com.game.wargame.Model.Entities.EntitiesModel;
import com.game.wargame.Model.Entities.Entity;
import com.game.wargame.Model.Entities.Explosion;
import com.game.wargame.Model.Entities.Players.LocalPlayerModel;
import com.game.wargame.Model.Entities.Players.PlayerException;
import com.game.wargame.Model.Entities.Players.PlayerModel;
import com.game.wargame.Model.Entities.Projectiles.Projectile;
import com.game.wargame.Model.Entities.VirtualMap.RealCell;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by sergei on 24/02/16.
 */
public class CollisionManager {

    IDistanceCalculator mDistanceCalculator;

    public CollisionManager(IDistanceCalculator distanceCalculator) {
        mDistanceCalculator = distanceCalculator;
    }

    public void treatLocalPlayerAndExplosionCollision(LocalPlayerModel player, EntitiesModel entitiesModel, double time) {
        ArrayList<Explosion> explosions = entitiesModel.getExplosions();
        for (Explosion explosion : explosions) {
            if (areColliding(player, explosion))
            {
                try {
                    //a collision with an explosion kills the player
                    if (explosion.getOwner() != player.getPlayerId()) {
                        player.setHealth(0);
                        player.die(explosion.getOwner(), time);
                    }
                }
                catch (PlayerException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    public void treatBlockCollisions(EntitiesModel entitiesModel) {
        ArrayList<RealCell> realCells = entitiesModel.getRealCells();
        ArrayList<Projectile> projectiles = entitiesModel.getProjectiles();
        boolean collision = false;

        for (RealCell realCell : realCells) {
            for(Projectile projectile: projectiles) {
                if(PolyUtil.containsLocation(projectile.getPosition(), realCell.vertices(), false)) {
                    realCell.setToRemove(true);
                }
            }
        }
    }

    private boolean areColliding(PlayerModel p, Entity e)
    {
        float[] results = new float[1];
        LatLng entityPosition = e.getPosition();
        LatLng playerPosition = p.getPosition();
        mDistanceCalculator.distanceBetween(playerPosition.latitude, playerPosition.longitude, entityPosition.latitude, entityPosition.longitude, results);

        return results[0] < e.getRadius();
    }

}
