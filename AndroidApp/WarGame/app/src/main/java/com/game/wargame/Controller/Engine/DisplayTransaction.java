package com.game.wargame.Controller.Engine;

import com.game.wargame.Views.Views.GameView;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by clement on 14/04/16.
 */
public class DisplayTransaction {

    private LinkedBlockingQueue<DisplayCommand> mDisplayCommands;

    public DisplayTransaction() {
        mDisplayCommands = new LinkedBlockingQueue<>();
    }

    public void add(DisplayCommand displayCommand) {
        try {
            mDisplayCommands.put(displayCommand);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void commit(GameView gameView) {
        Iterator<DisplayCommand> it = mDisplayCommands.iterator();

        while(it.hasNext()) {
            DisplayCommand command = it.next();
            command.execute(gameView);
        }
        mDisplayCommands.clear();
    }
}
