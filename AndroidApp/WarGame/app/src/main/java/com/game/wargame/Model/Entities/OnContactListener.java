package com.game.wargame.Model.Entities;

import com.game.wargame.Model.Entities.ProjectileModel;

/**
 * Created by sergei on 01/03/16.
 */
public interface OnContactListener {

    public void onContact(ProjectileModel projectile);
    public void onContact(Player player);
    public void onContact(Tile tile);

}
