package com.game.wargame.Model.Entities;

import com.game.wargame.Model.Entities.Entity;
import com.game.wargame.Model.Entities.Projectiles.Projectile;
import com.game.wargame.Model.Entities.VirtualMap.RealCell;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by sergei on 01/03/16.
 */

public class EntitiesModel implements Updatable {

    protected Lock mLock = new ReentrantLock();

    protected ArrayList<Projectile> mProjectiles = new ArrayList<>();
    protected ArrayList<RealCell> mRealCells = new ArrayList<>();
    protected ArrayList<Explosion> mExplosions = new ArrayList<>();

    public EntitiesModel() {
    }

    public ArrayList<Entity> getEntities() {
        ArrayList<Entity> entities = new ArrayList<>();

        mLock.lock();
        entities.addAll(mRealCells);
        entities.addAll(mProjectiles);
        entities.addAll(mExplosions);
        mLock.unlock();

        return entities;
    }

    public ArrayList<RealCell> getRealCells() {
        ArrayList<RealCell> entities = new ArrayList<>();

        mLock.lock();
        entities.addAll(mRealCells);
        mLock.unlock();

        return entities;
    }

    public ArrayList<Projectile> getProjectiles() {
        ArrayList<Projectile> entities = new ArrayList<>();

        mLock.lock();
        entities.addAll(mProjectiles);
        mLock.unlock();

        return entities;
    }

    public ArrayList<Explosion> getExplosions() {
        ArrayList<Explosion> entities = new ArrayList<>();

        mLock.lock();
        entities.addAll(mExplosions);
        mLock.unlock();

        return entities;
    }

    public void addProjectile(Projectile projectile) {
        mLock.lock();
        mProjectiles.add(projectile);
        mLock.unlock();
    }

    public void removeProjectile(Projectile projectile) {
        mLock.lock();
        mProjectiles.remove(projectile);
        mLock.unlock();
    }

    public void addExplosion(Explosion explosion) {
        mLock.lock();
        mExplosions.add(explosion);
        mLock.unlock();
    }

    public void removeExplosion(Explosion explosion) {
        mLock.lock();
        mExplosions.remove(explosion);
        mLock.unlock();
    }

    public void addRealCell(RealCell realCell) {
        mLock.lock();
        mRealCells.add(realCell);
        mLock.unlock();
    }

    public void removeRealCell(RealCell realCell) {
        mLock.lock();
        mRealCells.remove(realCell);
        mLock.unlock();
    }


    public void update(long ticks, int increment) {
        ArrayList<RealCell> realCells = new ArrayList<>();
        ArrayList<Projectile> projectiles = new ArrayList<>();
        ArrayList<Explosion> explosions = new ArrayList<>();
        mLock.lock();
        projectiles.addAll(mProjectiles);
        realCells.addAll(mRealCells);

        for (RealCell realCell : realCells) {
            realCell.update(ticks, increment);
            if (realCell.isToRemove() && !realCell.isDisplayed())
                mRealCells.remove(realCell);
        }

        for (Projectile projectile : projectiles) {
            projectile.update(ticks, increment);
            if (projectile.isToRemove() && !projectile.isDisplayed())
                mProjectiles.remove(projectile);
        }

        for (Explosion explosion : explosions) {
            explosion.update(ticks, increment);
            if (explosion.isToRemove() && !explosion.isDisplayed())
                mProjectiles.remove(explosion);
        }

        mLock.unlock();
    }

    public void setDisplayed(Entity entity, boolean isDisplayed) {
        mLock.lock();
        entity.setDisplayed(isDisplayed);
        mLock.unlock();
    }
}
