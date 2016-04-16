package com.game.wargame.Controller.GameLogic;

import com.game.wargame.Model.Entities.Entity;
import com.game.wargame.Model.Entities.Projectiles.Projectile;

/**
 * Created by sergei on 15/03/16.
 */
public interface OnExplosionListener {

    public void onExplosion(Projectile projectile, long time);
}
