package com.game.wargame.Model;

import com.game.wargame.Model.FragManager;

/**
 * Created by sergei on 18/03/16.
 */
public class GameContext {

    private static final int GAME_DURATION = 60000;

    private boolean mStarted;
    private int mTimeStart;
    private int mCurrentTime;
    private FragManager mFragManager;

    public GameContext(FragManager fragManager)
    {
        mFragManager = fragManager;
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
        } else {
            mCurrentTime += increment;
        }
    }

    public int getRemainingTime() {
        return (GAME_DURATION-(mCurrentTime-mTimeStart))/1000;
    }

    public void addFrag(String id) {
        mFragManager.addFrag(id);
    }

    public void addDeath(String id) {
        mFragManager.addDeath(id);
    }
}
