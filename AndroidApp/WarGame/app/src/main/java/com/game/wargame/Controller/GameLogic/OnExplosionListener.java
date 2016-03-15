package com.game.wargame.Controller.GameLogic;

import com.game.wargame.Model.Entities.Entity;

/**
 * Created by sergei on 15/03/16.
 */
public interface OnExplosionListener {

    public void onExplosion(Entity entity, long time);
}
