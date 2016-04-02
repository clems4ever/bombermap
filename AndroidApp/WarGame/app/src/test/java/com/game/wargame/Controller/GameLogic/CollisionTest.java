package com.game.wargame.Controller.GameLogic;

import com.game.wargame.Controller.Communication.Game.LocalPlayerSocket;
import com.game.wargame.Model.Entities.LocalPlayerModel;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Created by sergei on 20/03/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class CollisionTest {

    LocalPlayerModel mPlayerModel;

    @Mock
    LocalPlayerSocket mLocalPlayerSocket;

    @Before
    public void setUp() {
        mPlayerModel = new LocalPlayerModel("player_id", mLocalPlayerSocket);
    }

    public void testThat() {

    }

}
