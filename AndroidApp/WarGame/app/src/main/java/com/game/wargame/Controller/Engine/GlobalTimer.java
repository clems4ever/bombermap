package com.game.wargame.Controller.Engine;

import android.app.Activity;
import android.util.Log;

import com.game.wargame.Model.Entities.EntitiesModel;
import com.game.wargame.Model.Entities.Entity;
import com.game.wargame.Model.Entities.Projectile;
import com.game.wargame.Views.GameView;

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
    private Activity mActivity;
    private IUpdateCallback mUpdateCallback;
    private GameView mGameView;
    private EntitiesModel mEntities;

    public final int UPDATE_SAMPLE_TIME = 50;
    public final int SERVER_SAMPLE_TIME = 1000;

    private void startTimer(IUpdateCallback updateCallback) {
        mTimer = new Timer(false);

        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                mTicks++;
                mUpdateCallback.update(mEntities, mTicks, UPDATE_SAMPLE_TIME);
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mGameView.display(mEntities);
                    }
                });

            }
        }, 0, UPDATE_SAMPLE_TIME);
    }

    public GlobalTimer(Activity activity) {
        mActivity = activity;
    }

    public void start() {
        startTimer(mUpdateCallback);
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

    public void setEntitiesModel(EntitiesModel entities)
    {
        mEntities = entities;
    }

    public void setGameView(GameView gameView)
    {
        mGameView = gameView;
    }

    public void setUpdateCallback(IUpdateCallback updateCallback) {
        mUpdateCallback = updateCallback;
    }
}
