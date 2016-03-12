package com.game.wargame.Controller.Engine;

import android.app.Activity;
import android.provider.Settings;

import com.game.wargame.Model.Entities.Projectile;

import java.util.ArrayList;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import com.game.wargame.Model.Entities.ProjectileModel;
import com.game.wargame.Views.GameView;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by sergei on 01/03/16.
 */
public class ProjectilesUpdateTimer extends Timer {
    private Activity mActivity;
    private Lock mLock = new ReentrantLock();
    private ProjectileModel mProjectileModel;
    private ArrayList<Projectile> mProjectilesToRemove;

    private Timer mTimer;
    private GameView mGameView;
    private long mTime;

    private static final int UPDATE_SAMPLE_TIME = 100;

    public ProjectilesUpdateTimer(Activity activity) {
        mActivity = activity;
    }

    public void setProjectileModel(ProjectileModel projectileModel) {
        mProjectileModel = projectileModel;
        mProjectilesToRemove = new ArrayList<>();
    }

    public void setGameView(GameView gameView) {
        mGameView = gameView;
    }


    public void start() {
        startTimer();
    }

    public void stop() {
        stopTimer();
    }

    public double getTime() {
        return (double)GlobalTimer.getTicks()*GlobalTimer.UPDATE_SAMPLE_TIME;
    }

    public void updateProjectile(Projectile projectile, double time) {
        TreeMap<Double, LatLng> trajectory = projectile.getTrajectory();
        Double newTimestamp = trajectory.ceilingKey(time);

        if (newTimestamp != null) {
            LatLng newPosition = trajectory.get(newTimestamp);
            projectile.setPosition(newPosition);
            //TODO: evaluateContacts
        }
        else {
            projectile.setToDestroy(true);
            mProjectilesToRemove.add(projectile);
        }
    }

    private void startTimer() {
        mTimer = new Timer(false);

        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                mLock.lock();
                ArrayList<Projectile> projectiles = mProjectileModel.getProjectiles();
                ArrayList<Projectile> toRemove = new ArrayList<Projectile>();
                double time = getTime();

                mProjectilesToRemove.clear();
                for (Projectile projectile : projectiles)
                {
                    updateProjectile(projectile, time);
                }

                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ArrayList<Projectile> projectiles = mProjectileModel.getProjectiles();
                        mGameView.renderProjectiles(projectiles);
                        for (Projectile projectile : mProjectilesToRemove)
                        {
                            mProjectileModel.removeProjectile(projectile);
                        }
                    }
                });

                mLock.unlock();
            }
        }, 0, UPDATE_SAMPLE_TIME);
    }

    private void stopTimer() {
        if(mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
        }
    }

}
