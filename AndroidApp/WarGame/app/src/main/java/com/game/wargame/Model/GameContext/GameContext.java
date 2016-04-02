package com.game.wargame.Model.GameContext;

import java.util.ArrayList;

/**
 * Created by sergei on 18/03/16.
 */
public class GameContext {

    private static final int GAME_DURATION = 60000;

    private boolean mStarted;
    private int mTimeStart;
    private int mCurrentTime;
    private FragManager mFragManager;
    private GameNotificationManager mGameNotificationManager;

    public GameContext(FragManager fragManager, GameNotificationManager gameNotificationManager) {
        mFragManager = fragManager;
        mGameNotificationManager = gameNotificationManager;
    }

    public void setTimeStart(int timeStart) {
        mTimeStart = timeStart;
    }

    public void setCurrentTime(int currentTime) {
        mCurrentTime = currentTime;
    }

    public boolean isStarted() { return mStarted; }

    public boolean toEnd() {
        return (mCurrentTime - mTimeStart) > GAME_DURATION;
    }

    public void update(long ticks, int increment) {
        if (!isStarted()) {
            mTimeStart = (int)ticks*increment;
            mCurrentTime = mTimeStart;
            mStarted = true;
        }
        mCurrentTime += increment;
        purgeNotifications(mCurrentTime);
    }

    public int getRemainingTime() {
        return (GAME_DURATION-(mCurrentTime-mTimeStart))/1000;
    }

    public void handleFrag(String dead, String killer, double time) {
        mFragManager.addFrag(killer);
        mFragManager.addDeath(dead);
        GameNotification gameNotification = new GameNotification(killer+" killed "+dead+" savagely", time);
        mGameNotificationManager.pushNotification(gameNotification);
    }

    public ArrayList<GameNotification> getNotificationsToDisplay() {
        ArrayList<GameNotification> gameNotifications = new ArrayList<>();
        mGameNotificationManager.getNotificationsToDisplay(gameNotifications);
        return gameNotifications;
    }

    public void purgeNotifications(double time) {
        mGameNotificationManager.removeUnusedNotifications(time);
    }

}
