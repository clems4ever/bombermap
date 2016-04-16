package com.game.wargame.Controller.Engine.DisplayCommands;

import com.game.wargame.Controller.Engine.DisplayCommand;
import com.game.wargame.Model.Entities.Explosion;
import com.game.wargame.Views.Views.GameView;

/**
 * Created by clement on 16/04/16.
 */
public class UpdateExplosionDisplayCommand extends DisplayCommand {

    private Explosion mExplosion;

    public UpdateExplosionDisplayCommand(Explosion explosion) {
        mExplosion = explosion;
    }

    @Override
    public void execute(GameView gameView) {
        gameView.updateEntity(mExplosion);
    }

}
