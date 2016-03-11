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
public class GlobalTimer extends Timer {

    private static Timer mTimer;
    private static long mTicks = 0;
    private static Lock mLock = new ReentrantLock();

    public static final int UPDATE_SAMPLE_TIME = 100;

    private static void startTimer() {
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

    public static void start() {
        startTimer();
    }

    public static void stop() {
        stopTimer();
    }

    public static long getTicks()
    {
        long ticks = 0;

        mLock.lock();
        ticks = mTicks;
        mLock.unlock();

        return ticks;
    }

    private static void stopTimer() {
        if(mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
        }
    }

}
