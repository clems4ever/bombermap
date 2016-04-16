package com.game.wargame.Controller.Engine.DisplayCommands;

import com.game.wargame.Controller.Engine.DisplayCommand;
import com.game.wargame.Model.Entities.Explosion;
import com.game.wargame.Views.Animations.ExplosionAnimation;
import com.game.wargame.Views.Views.GameView;

/**
 * Created by clement on 16/04/16.
 */
public class AddExplosionDisplayCommand extends DisplayCommand {

    private Explosion mExplosion;

    public AddExplosionDisplayCommand(Explosion explosion) {
        mExplosion = explosion;
    }

    @Override
    public void execute(GameView gameView) {
        gameView.addEntity(mExplosion);
    }
}
