package com.game.wargame.Model.Entities;

import com.game.wargame.Model.Entities.Projectile;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by sergei on 01/03/16.
 */

public class ProjectileModel {

    public static ArrayList<Projectile> getProjectiles() {
        ArrayList<Projectile> projectiles = new ArrayList<>();

        mLock.lock();
        projectiles.addAll(mProjectiles);
        mLock.unlock();

        return projectiles;
    }

    public static void addProjectile(Projectile projectile) {
        mLock.lock();
        mProjectiles.add(projectile);
        mLock.unlock();
    }

    public static void removeProjectile(Projectile projectile) {
        mLock.lock();
        mProjectiles.remove(projectile);
        mLock.unlock();
    }


    protected static Lock mLock = new ReentrantLock();
    protected static ArrayList<Projectile> mProjectiles = new ArrayList<>();
}
