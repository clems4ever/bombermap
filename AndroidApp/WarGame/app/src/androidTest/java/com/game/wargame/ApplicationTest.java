package com.game.wargame;

import android.app.Application;
import android.content.Context;
import android.support.test.runner.AndroidJUnit4;
import android.test.ApplicationTestCase;

import com.game.wargame.Communication.GameEngineSocket;
import com.game.wargame.Communication.PlayerSocket;
import com.game.wargame.Entities.LocalPlayerModel;
import com.game.wargame.Entities.Player;
import com.game.wargame.GameEngine.GameEngine;
import com.game.wargame.GameEngine.GameView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
@RunWith(AndroidJUnit4.class)
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }


}