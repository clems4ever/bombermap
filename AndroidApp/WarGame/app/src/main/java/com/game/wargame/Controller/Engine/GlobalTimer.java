package com.game.wargame.Controller.Engine;

import com.game.wargame.Model.Entities.Projectile;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by sergei on 11/03/16.
 */
public class GlobalTimer extends Timer implements OnClockEventListener {

    private Timer mTimer;
    private long mTicks = 0;
    private Lock mLock = new ReentrantLock();

    public final int UPDATE_SAMPLE_TIME = 100;
    public final int SERVER_SAMPLE_TIME = 1000;

    private void startTimer() {
        mTimer = new Timer(false);

        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                mLock.lock();
                mTicks++;
                mLock.unlock();
            }
        }, 0, UPDATE_SAMPLE_TIME);
    }

    public void start() {
        startTimer();
    }

    public void stop() {
        stopTimer();
    }

    public long getTicks()
    {
        long ticks = 0;

        mLock.lock();
        ticks = mTicks;
        mLock.unlock();

        return ticks;
    }

    private void stopTimer() {
        if(mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
        }
    }

    public void setClock(long ticks) {
        mLock.lock();
        mTicks = ticks*(SERVER_SAMPLE_TIME/UPDATE_SAMPLE_TIME);
        mLock.unlock();
    }

}
