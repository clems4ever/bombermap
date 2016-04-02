package com.game.wargame.Model.GameContext;

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

    GameContext mGameContext;

    @Before
    public void setUp() {
        mGameContext = new GameContext(mFragManager, new GameNotificationManager());
    }

    @Test
    public void testThatNotificationIsAddedOnFrag() {
        mGameContext.handleFrag("killer", "killee", 100);
        assertEquals(1, mGameContext.getNotificationsToDisplay().size());
    }

    @Test
    public void testThatThreeNotificationsAreReturned() {
        mGameContext.handleFrag("killer", "killee", 100);
        mGameContext.handleFrag("killer", "killee", 100);
        mGameContext.handleFrag("killer", "killee", 100);
        mGameContext.handleFrag("killer", "killee", 100);
        assertEquals(3, mGameContext.getNotificationsToDisplay().size());
    }

    @Test
    public void testThatNotificationsArePurged() {
        mGameContext.handleFrag("killer", "killee", 0);
        mGameContext.handleFrag("killer", "killee", 1);
        mGameContext.update(0, 200);
        assertEquals(1, mGameContext.getNotificationsToDisplay().size());
    }

    @Test
    public void testThatNotificationsAreSorted() {
        mGameContext.handleFrag("killer1", "killee1", 2);
        mGameContext.handleFrag("killer2", "killee2", 1);
        mGameContext.handleFrag("killer3", "killee3", 0);
        mGameContext.handleFrag("killer1", "killee1", 4);
        mGameContext.handleFrag("killer1", "killee1", 5);
        assertEquals(0, (int) mGameContext.getNotificationsToDisplay().get(0).getStartTime());
        assertEquals(1, (int)mGameContext.getNotificationsToDisplay().get(1).getStartTime());
        assertEquals(2, (int)mGameContext.getNotificationsToDisplay().get(2).getStartTime());
    }

}
