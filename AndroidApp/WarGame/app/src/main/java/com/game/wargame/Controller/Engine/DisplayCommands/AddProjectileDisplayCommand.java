package com.game.wargame.Controller.Engine.DisplayCommands;

import com.game.wargame.Controller.Engine.DisplayCommand;
import com.game.wargame.Controller.Engine.DisplayTransaction;
import com.game.wargame.Model.Entities.Projectiles.Projectile;
import com.game.wargame.Views.Views.GameView;

/**
 * Created by clement on 15/04/16.
 */
public class AddProjectileDisplayCommand extends DisplayCommand {

    private Projectile mProjectile;

    public AddProjectileDisplayCommand(Projectile projectile) {
        mProjectile = projectile;
    }

    @Override
    public void execute(GameView gameView) {
        gameView.addEntity(mProjectile);
    }
}
