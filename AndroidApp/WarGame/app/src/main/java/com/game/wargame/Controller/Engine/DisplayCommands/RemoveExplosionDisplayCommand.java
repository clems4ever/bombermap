package com.game.wargame.Controller.Engine.DisplayCommands;

import com.game.wargame.Controller.Engine.DisplayCommand;
import com.game.wargame.Model.Entities.Explosion;
import com.game.wargame.Views.Views.GameView;

/**
 * Created by clement on 16/04/16.
 */
public class RemoveExplosionDisplayCommand extends DisplayCommand {

    private Explosion mExplosion;

    public RemoveExplosionDisplayCommand(Explosion explosion) {
        mExplosion = explosion;
    }

    @Override
    public void execute(GameView gameView) {
        gameView.removeEntity(mExplosion);
    }

}
