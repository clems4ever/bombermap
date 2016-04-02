package com.game.wargame.Controller.Engine;

import android.app.Activity;

import com.game.wargame.Controller.GameLogic.CollisionManager;
import com.game.wargame.Model.Entities.LocalPlayerModel;
import com.game.wargame.Model.GameContext;
import com.game.wargame.Model.Entities.EntitiesModel;
import com.game.wargame.Model.Entities.PlayerModel;
import com.game.wargame.Views.GameView;

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
    private CollisionManager mCollisionManager;
    private GameView mGameView;

    private EntitiesModel mEntities;
    private LocalPlayerModel mCurrentPlayer;
    private GameContext mGameContext;

    public final int UPDATE_SAMPLE_TIME = 50;
    public final int SERVER_SAMPLE_TIME = 1000;
    public final int GAME_TOTAL_TIME = 60000;

    private void startTimer(IUpdateCallback updateCallback) {
        mTimer = new Timer(false);

        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!mGameContext.toEnd()) {
                    mTicks++;
                    double time = mTicks * UPDATE_SAMPLE_TIME;

                    mUpdateCallback.update(mEntities, mTicks, UPDATE_SAMPLE_TIME);
                    mUpdateCallback.update(mGameContext, mTicks, UPDATE_SAMPLE_TIME);

                    mCollisionManager.treatPlayerEntitiesCollisions(mEntities,
                            mCurrentPlayer,
                            time);

                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mGameView.display(mEntities);
                            mGameView.display(mGameContext);
                        }
                    });
                } else {
                    //TODO: end the game
                }
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

    public void setCurrentPlayerModel(LocalPlayerModel player)
    {
        mCurrentPlayer = player;
    }

    public void setGameView(GameView gameView)
    {
        mGameView = gameView;
    }

    public void setCollisionManager(CollisionManager collisionManager) {
        mCollisionManager = collisionManager;
    }

    public void setGameContext(GameContext gameContext) {
        mGameContext = gameContext;
    }

    public void setUpdateCallback(IUpdateCallback updateCallback) {
        mUpdateCallback = updateCallback;
    }
}
