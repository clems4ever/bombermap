package com.game.wargame.Model.Entities.Projectiles;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by sergei on 01/03/16.
 */

public class ProjectileModel {

    public ProjectileModel() {

    }

    public ArrayList<Projectile> getProjectiles() {
        ArrayList<Projectile> projectiles = new ArrayList<>();

        mLock.lock();
        projectiles.addAll(mProjectiles);
        mLock.unlock();

        return projectiles;
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


    protected Lock mLock = new ReentrantLock();
    protected ArrayList<Projectile> mProjectiles = new ArrayList<>();
}
