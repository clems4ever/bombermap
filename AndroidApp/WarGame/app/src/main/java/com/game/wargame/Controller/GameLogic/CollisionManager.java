package com.game.wargame.Controller.GameLogic;

import com.game.wargame.Controller.Engine.DisplayCommands.AddExplosionDisplayCommand;
import com.game.wargame.Controller.Engine.DisplayCommands.RemoveBlockDisplayCommand;
import com.game.wargame.Controller.Engine.DisplayCommands.RemoveProjectileDisplayCommand;
import com.game.wargame.Controller.Engine.DisplayTransaction;
import com.game.wargame.Controller.Utils.IDistanceCalculator;
import com.game.wargame.Model.Entities.EntitiesContainer;
import com.game.wargame.Model.Entities.Entity;
import com.game.wargame.Model.Entities.Explosion;
import com.game.wargame.Model.Entities.Players.LocalPlayerModel;
import com.game.wargame.Model.Entities.Players.PlayerException;
import com.game.wargame.Model.Entities.Players.PlayerModel;
import com.game.wargame.Model.Entities.Projectiles.Projectile;
import com.game.wargame.Model.Entities.VirtualMap.RealCell;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by sergei on 24/02/16.
 */
public class CollisionManager {

    IDistanceCalculator mDistanceCalculator;

    public CollisionManager(IDistanceCalculator distanceCalculator) {
        mDistanceCalculator = distanceCalculator;
    }

    public void treatLocalPlayerAndExplosionCollision(LocalPlayerModel player, List<Explosion> explosions, double time) {
        Iterator<Explosion> it = explosions.iterator();
        while(it.hasNext()) {
            Explosion explosion = it.next();
            if (areLocalPlayerAndEntityColliding(player, explosion))
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

    public void treatBlockCollisions(EntitiesContainer entitiesContainer, double time, DisplayTransaction displayTransaction) {
        ArrayList<RealCell> realCells = entitiesContainer.getRealCells();
        ArrayList<Projectile> projectiles = entitiesContainer.getProjectiles();

        for(Projectile projectile: projectiles) {
            for (RealCell realCell : realCells) {
                if(PolyUtil.containsLocation(projectile.getPosition(), realCell.vertices(), false)) {
                    Explosion explosion = new Explosion(projectile.getOwner(), time, projectile.getPosition(), projectile.getDirection());
                    entitiesContainer.addExplosion(explosion);
                    entitiesContainer.removeProjectile(projectile);
                    entitiesContainer.removeBlock(realCell);

                    displayTransaction.add(new AddExplosionDisplayCommand(explosion));
                    displayTransaction.add(new RemoveProjectileDisplayCommand(projectile));
                    displayTransaction.add(new RemoveBlockDisplayCommand(realCell));
                }
            }
        }
    }

    private boolean areLocalPlayerAndEntityColliding(PlayerModel p, Entity e)
    {
        float[] results = new float[1];
        LatLng entityPosition = e.getPosition();
        LatLng playerPosition = p.getPosition();
        mDistanceCalculator.distanceBetween(playerPosition.latitude, playerPosition.longitude, entityPosition.latitude, entityPosition.longitude, results);

        return results[0] < e.getRadius();
    }

}
