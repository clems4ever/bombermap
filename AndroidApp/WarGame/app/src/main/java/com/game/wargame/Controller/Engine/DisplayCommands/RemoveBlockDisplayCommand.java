package com.game.wargame.Controller.Engine.DisplayCommands;

import com.game.wargame.Controller.Engine.DisplayCommand;
import com.game.wargame.Model.Entities.VirtualMap.RealCell;
import com.game.wargame.Views.Views.GameView;

/**
 * Created by clement on 15/04/16.
 */
public class RemoveBlockDisplayCommand extends DisplayCommand {

    private RealCell mBlock;

    public RemoveBlockDisplayCommand(RealCell block) {
        mBlock = block;
    }

    @Override
    public void execute(GameView gameView) {
        gameView.removeBlock(mBlock);
    }
}
