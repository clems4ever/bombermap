package com.game.wargame;

import android.app.Application;

import com.game.wargame.Controller.GameEngine;

/**
 * Created by clement on 15/02/16.
 */
public class WarGameApplication extends Application {

    private GameEngine mGameEngine;

    private boolean mDebugMode = false;

    public void setGameEngine(GameEngine gameEngine) {
        mGameEngine = gameEngine;
    }

    public GameEngine getGameEngine() {
        return mGameEngine;
    }
}
