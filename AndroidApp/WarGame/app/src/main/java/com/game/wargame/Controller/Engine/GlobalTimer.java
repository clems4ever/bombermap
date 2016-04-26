package com.game.wargame.Controller.Engine;

import android.app.Activity;

import com.game.wargame.AppConstant;
import com.game.wargame.Controller.Engine.DisplayCommands.AddBlockDisplayCommand;
import com.game.wargame.Controller.GameLogic.CollisionManager;
import com.game.wargame.Model.Entities.EntitiesContainer;
import com.game.wargame.Model.Entities.EntitiesContainerUpdater;
import com.game.wargame.Model.Entities.Entity;
import com.game.wargame.Model.Entities.Explosion;
import com.game.wargame.Model.Entities.Players.LocalPlayerModel;
import com.game.wargame.Model.Entities.Players.RemotePlayerModel;
import com.game.wargame.Model.Entities.Projectiles.Projectile;
import com.game.wargame.Model.Entities.VirtualMap.RealCell;
import com.game.wargame.Model.GameContext.GameContext;
import com.game.wargame.Views.Activities.GameMainFragment;
import com.game.wargame.Views.Views.GameView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by sergei on 11/03/16.
 */
public class GlobalTimer extends Timer implements OnClockEventListener {

    private Timer mTimer;
    private TimerTask mTimerTask;
    private long mTicks = 0;
    private Lock mLock = new ReentrantLock();
    private Activity mActivity;
    private boolean isStopped = false;

    private CollisionManager mCollisionManager;

    private EntitiesContainer mEntities;
    private EntitiesContainerUpdater mEntitiesContainerUpdater;

    private LocalPlayerModel mCurrentPlayer;
    private ArrayList<RemotePlayerModel> mRemotePlayerModels;
    private GameContext mGameContext;
    private IDisplayCallback mDisplayCallback;
    private DisplayTransaction mDisplayTransaction = new DisplayTransaction();
    private GameView mGameView;

    private GameMainFragment.Callback mGameCallback;

    public final int UPDATE_SAMPLE_TIME = 50;
    public final int SERVER_SAMPLE_TIME = 1000;

    public GlobalTimer(Activity activity) {
        mActivity = activity;
        mRemotePlayerModels = new ArrayList<>();
        mEntitiesContainerUpdater = new EntitiesContainerUpdater();
    }

    private void startTimer() {
        mTimer = new Timer(false);

        Iterator<RealCell> entityIterator = mEntities.getRealCells().iterator();
        while(entityIterator.hasNext()) {
            RealCell r = entityIterator.next();
            mDisplayTransaction.add(new AddBlockDisplayCommand(r));
        }

        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                if (!mGameContext.toEnd()) {
                    //mTicks++;
                    double time = mTicks * UPDATE_SAMPLE_TIME;

                    for(Entity e : mEntities.getEntities()) {
                        e.update(mTicks, UPDATE_SAMPLE_TIME, mEntitiesContainerUpdater, mDisplayTransaction);
                    }
                    mGameContext.update(mTicks, UPDATE_SAMPLE_TIME, mEntitiesContainerUpdater, mDisplayTransaction);

                    if (!AppConstant.DEMO)
                        mCurrentPlayer.update(mTicks, UPDATE_SAMPLE_TIME, mEntitiesContainerUpdater, mDisplayTransaction);

                    for (RemotePlayerModel remotePlayerModel : mRemotePlayerModels) {
                        remotePlayerModel.update(mTicks, UPDATE_SAMPLE_TIME, mEntitiesContainerUpdater, mDisplayTransaction);
                    }

                    List<Explosion> explosions = mEntities.getExplosions();

                    if (!AppConstant.DEMO)
                        mCollisionManager.treatLocalPlayerAndExplosionCollision(mCurrentPlayer, explosions, time);
                    mCollisionManager.treatBlockCollisions(mEntities, mEntitiesContainerUpdater, time, mDisplayTransaction);

                    mEntitiesContainerUpdater.update(mEntities);

                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mDisplayTransaction.commit(mGameView);
                            mDisplayCallback.display();
                        }
                    });
                } else {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!isStopped && mGameCallback != null) { //TODO : find a way around this, UGLY
                                mGameCallback.onGameFinish(mGameContext);
                                stop();
                                isStopped = true;
                            }
                        }
                    });
                }
            }
        };
        mTimer.scheduleAtFixedRate(mTimerTask, 0, UPDATE_SAMPLE_TIME);
    }

    public void start() {
        startTimer();
    }

    public void stop() {
        stopTimer();
    }

    public void setView(GameView gameView) {
        mGameView = gameView;
    }

    public void scheduleDisplayCommand(DisplayCommand displayCommand) {
        mDisplayTransaction.add(displayCommand);
    }

    public long getTicks()
    {
        long ticks = 0;

        mLock.lock();
        ticks = mTicks;
        mLock.unlock();

        return ticks;
    }

    public long getTime()
    {
        long time = 0;

        mLock.lock();
        time = mTicks * UPDATE_SAMPLE_TIME;
        mLock.unlock();

        return time;
    }

    private void stopTimer() {
        if (mTimerTask != null) {
            mTimerTask.cancel();
        }

        if(mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
        }
    }

    public void setClock(long ticks) {
        mLock.lock();
        mTicks = ticks*(SERVER_SAMPLE_TIME/UPDATE_SAMPLE_TIME);
        if (!mGameContext.isStarted())
            mGameContext.start(ticks*SERVER_SAMPLE_TIME);
        mLock.unlock();
    }

    @Override
    public void setGameStartTime(double time) {
        mLock.lock();
        if ((int)time != -1) {
            if ((int)mGameContext.getTimeStart() != -1)
                time = Math.min(time, mGameContext.getTimeStart());
            mGameContext.setTimeStart(time);
        }
        mLock.unlock();
    }

    public void setEntitiesModel(EntitiesContainer entities)
    {
        mEntities = entities;
    }

    public void setCurrentPlayerModel(LocalPlayerModel player)
    {
        mCurrentPlayer = player;
    }

    public void addRemotePlayer(RemotePlayerModel remotePlayerModel) {
        mRemotePlayerModels.add(remotePlayerModel);
    }

    public void setCollisionManager(CollisionManager collisionManager) {
        mCollisionManager = collisionManager;
    }

    public void setGameContext(GameContext gameContext) {
        mGameContext = gameContext;
    }

    public void setGameCallback(GameMainFragment.Callback gameCallback) {
        this.mGameCallback = gameCallback;
    }

    public void setDisplayCallback(IDisplayCallback displayCallback) {
        mDisplayCallback = displayCallback;
    }
}
