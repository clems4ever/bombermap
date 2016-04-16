package com.game.wargame.Model.GameContext;

import com.game.wargame.Controller.Engine.DisplayTransaction;
import com.game.wargame.Model.Entities.EntitiesContainer;
import com.game.wargame.Model.Entities.EntitiesContainerUpdater;
import com.game.wargame.Model.Entities.Players.Player;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import static org.junit.Assert.assertEquals;

/**
 * Created by sergei on 02/04/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class GameContextTest {

    @Mock
    FragManager mFragManager;
    @Mock
    DisplayTransaction mMockDisplayTransaction;
    @Mock
    EntitiesContainerUpdater mMockEntitiesContainerUpdater;

    GameContext mGameContext;

    GameNotification gn;

    Player mKiller;
    Player mKillee;


    @Before
    public void setUp() {
        mGameContext = new GameContext(mFragManager, new GameNotificationManager(), 10);
        mGameContext.start(0);
        mKiller = new Player("killer", "killer");
        mKillee = new Player("killee", "killee");
    }

    @Test
    public void testThatNotificationIsAddedOnFrag() {
        mGameContext.handleFrag(mKiller, mKillee, 100);
        assertEquals(1, mGameContext.getNotificationsToDisplay().size());
    }

    @Test
    public void testThatThreeNotificationsAreReturned() {
        mGameContext.handleFrag(mKiller, mKillee, 100);
        mGameContext.handleFrag(mKiller, mKillee, 100);
        mGameContext.handleFrag(mKiller, mKillee, 100);
        mGameContext.handleFrag(mKiller, mKillee, 100);
        assertEquals(3, mGameContext.getNotificationsToDisplay().size());
    }

    @Test
    public void testThatNotificationsArePurged() {
        mGameContext.handleFrag(mKiller, mKillee, 0);
        mGameContext.handleFrag(mKiller, mKillee, 1);
        mGameContext.update(1, gn.TIME_TO_DISPLAY, mMockEntitiesContainerUpdater, mMockDisplayTransaction);
        assertEquals(1, mGameContext.getNotificationsToDisplay().size());
    }

    @Test
    public void testThatNotificationsAreSorted() {
        mGameContext.handleFrag(mKiller, mKillee, 2);
        mGameContext.handleFrag(mKiller, mKillee, 1);
        mGameContext.handleFrag(mKiller, mKillee, 0);
        mGameContext.handleFrag(mKiller, mKillee, 4);
        mGameContext.handleFrag(mKiller, mKillee, 5);
        assertEquals(0, (int) mGameContext.getNotificationsToDisplay().get(0).getStartTime());
        assertEquals(1, (int)mGameContext.getNotificationsToDisplay().get(1).getStartTime());
        assertEquals(2, (int)mGameContext.getNotificationsToDisplay().get(2).getStartTime());
    }

}
