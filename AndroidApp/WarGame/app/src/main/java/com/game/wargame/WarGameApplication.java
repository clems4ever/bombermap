package com.game.wargame;

import android.app.Application;

import com.game.wargame.Controller.Communication.Game.GameSocket;
import com.game.wargame.Controller.Communication.Game.LocalPlayerSocket;
import com.game.wargame.Controller.GameEngine;

/**
 * Created by clement on 15/02/16.
 */
public class WarGameApplication extends Application {

    public GameEngine mGameEngine;
    public LocalPlayerSocket mLocalPlayerSocket;
    public GameSocket mGameSocket;

    private boolean mDebugMode = false;
}
