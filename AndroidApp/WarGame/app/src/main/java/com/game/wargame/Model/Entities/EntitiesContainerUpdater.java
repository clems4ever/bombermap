package com.game.wargame.Model.Entities;

import com.game.wargame.Model.Entities.Projectiles.Projectile;
import com.game.wargame.Model.Entities.VirtualMap.RealCell;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by clement on 16/04/16.
 */
public class EntitiesContainerUpdater {

    private LinkedList<Projectile> mToBeRemovedProjectiles;
    private LinkedList<Explosion> mToBeRemovedExplosions;
    private LinkedList<RealCell> mToBeRemovedRealCells;

    private LinkedList<Projectile> mToBeAddedProjectiles;
    private LinkedList<Explosion> mToBeAddedExplosions;
    private LinkedList<RealCell> mToBeAddedRealCells;

    public EntitiesContainerUpdater() {
        mToBeAddedProjectiles = new LinkedList<>();
        mToBeRemovedProjectiles = new LinkedList<>();

        mToBeAddedExplosions = new LinkedList<>();
        mToBeRemovedExplosions = new LinkedList<>();

        mToBeAddedRealCells = new LinkedList<>();
        mToBeRemovedRealCells = new LinkedList<>();
    }

    public void addProjectile(Projectile projectile) {
        mToBeAddedProjectiles.add(projectile);
    }

    public void removeProjectile(Projectile projectile) {
        mToBeRemovedProjectiles.add(projectile);
    }

    public void addExplosion(Explosion explosion) {
        mToBeAddedExplosions.add(explosion);
    }

    public void removeExplosion(Explosion explosion) {
        mToBeRemovedExplosions.add(explosion);
    }

    public void addBlock(RealCell realCell) {
        mToBeAddedRealCells.add(realCell);
    }

    public void removeBlock(RealCell realCell) {
        mToBeRemovedRealCells.add(realCell);
    }

    private void updateProjectiles(EntitiesContainer entitiesContainer) {
        Iterator<Projectile> it = mToBeAddedProjectiles.iterator();
        while(it.hasNext()) {
            Projectile p = it.next();
            entitiesContainer.addProjectile(p);
        }

        it = mToBeRemovedProjectiles.iterator();
        while(it.hasNext()) {
            Projectile p = it.next();
            entitiesContainer.removeProjectile(p);
        }

        mToBeAddedProjectiles.clear();
        mToBeRemovedProjectiles.clear();
    }

    private void updateExplosions(EntitiesContainer entitiesContainer) {
        Iterator<Explosion> it = mToBeAddedExplosions.iterator();
        while(it.hasNext()) {
            Explosion e = it.next();
            entitiesContainer.addExplosion(e);
        }

        it = mToBeRemovedExplosions.iterator();
        while(it.hasNext()) {
            Explosion e = it.next();
            entitiesContainer.removeExplosion(e);
        }

        mToBeAddedExplosions.clear();
        mToBeRemovedExplosions.clear();
    }

    private void updateBlocks(EntitiesContainer entitiesContainer) {
        Iterator<RealCell> it = mToBeAddedRealCells.iterator();
        while(it.hasNext()) {
            RealCell e = it.next();
            entitiesContainer.addBlock(e);
        }

        it = mToBeRemovedRealCells.iterator();
        while(it.hasNext()) {
            RealCell e = it.next();
            entitiesContainer.removeBlock(e);
        }

        mToBeAddedRealCells.clear();
        mToBeRemovedRealCells.clear();
    }


    public void update(EntitiesContainer entitiesContainer) {
        updateProjectiles(entitiesContainer);
        updateExplosions(entitiesContainer);
        updateBlocks(entitiesContainer);
    }
}
