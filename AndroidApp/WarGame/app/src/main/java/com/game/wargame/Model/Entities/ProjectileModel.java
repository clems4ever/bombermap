package com.game.wargame.Model.Entities;

import com.game.wargame.Model.Entities.Projectile;

import java.util.Set;

/**
 * Created by sergei on 01/03/16.
 */

public class ProjectileModel {

    public static Set<Projectile> getProjectiles() {
        return mProjectiles;
    }

    public static void addProjectile(Projectile projectile)
    {
        mProjectiles.add(projectile);
    }

    public static void removeProjectile(Projectile projectile)
    {
        mProjectiles.remove(projectile);
    }

    protected static Set<Projectile> mProjectiles;
}
