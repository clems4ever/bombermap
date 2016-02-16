package com.game.wargame;

import android.app.Application;

import com.game.wargame.GameEngine.GameEngine;
import com.google.android.gms.games.Game;

/**
 * Created by clement on 15/02/16.
 */
public class WarGameApplication extends Application {

    private GameEngine mGameEngine;


    public void setGameEngine(GameEngine gameEngine) {
        mGameEngine = gameEngine;
    }

    public GameEngine getGameEngine() {
        return mGameEngine;
    }
}
