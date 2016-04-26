package com.game.wargame.Controller.GameLogic;

import com.game.wargame.Controller.Engine.DisplayCommands.AddExplosionDisplayCommand;
import com.game.wargame.Controller.Engine.DisplayCommands.RemoveBlockDisplayCommand;
import com.game.wargame.Controller.Engine.DisplayCommands.RemoveProjectileDisplayCommand;
import com.game.wargame.Controller.Engine.DisplayTransaction;
import com.game.wargame.Controller.Utils.IDistanceCalculator;
import com.game.wargame.Model.Entities.EntitiesContainer;
import com.game.wargame.Model.Entities.EntitiesContainerUpdater;
import com.game.wargame.Model.Entities.Entity;
import com.game.wargame.Model.Entities.Explosion;
import com.game.wargame.Model.Entities.Players.LocalPlayerModel;
import com.game.wargame.Model.Entities.Players.PlayerException;
import com.game.wargame.Model.Entities.Players.PlayerModel;
import com.game.wargame.Model.Entities.Projectiles.Projectile;
import com.game.wargame.Model.Entities.VirtualMap.CellTypeEnum;
import com.game.wargame.Model.Entities.VirtualMap.RealCell;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
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

    public void treatBlockCollisions(final EntitiesContainer entitiesContainer, EntitiesContainerUpdater entitiesContainerUpdater, double time, DisplayTransaction displayTransaction) {
        ArrayList<RealCell> realCells = entitiesContainer.getRealCells();
        ArrayList<Projectile> projectiles = entitiesContainer.getProjectiles();
        ArrayList<Explosion> explosions = entitiesContainer.getExplosions();

        for(Projectile projectile: projectiles) {
            for (RealCell realCell : realCells) {
                if(PolyUtil.containsLocation(projectile.getPosition(), realCell.vertices(), true)) {
                    Explosion explosion = new Explosion(projectile.getOwner(), time, projectile.getPosition(), projectile.getDirection());
                    entitiesContainerUpdater.addExplosion(explosion);
                    entitiesContainerUpdater.removeProjectile(projectile);

                    displayTransaction.add(new AddExplosionDisplayCommand(explosion));
                    displayTransaction.add(new RemoveProjectileDisplayCommand(projectile));
                }
            }
        }

        for(Explosion explosion: explosions) {
            for (RealCell realCell : realCells) {
                if(realCell.cell().type() == CellTypeEnum.BREAKABLE_BLOCK && isExplosionCollidingBlock(explosion, realCell)) {
                    entitiesContainerUpdater.removeBlock(realCell);
                    displayTransaction.add(new RemoveBlockDisplayCommand(realCell));
                }
            }
        }
    }

    private boolean isExplosionCollidingBlock(Explosion e, RealCell r)
    {
        float[] results = new float[1];
        LatLng explosionPösition = e.getPosition();
        LatLng blockPosition = r.getPosition();
        mDistanceCalculator.distanceBetween(explosionPösition.latitude, explosionPösition.longitude, blockPosition.latitude, blockPosition.longitude, results);

        return results[0] < e.getRadius();
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
