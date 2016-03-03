package com.game.wargame.Controller;

import android.app.Activity;

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

    private Timer mTimer;
    private GameView mGameView;

    public ProjectilesUpdateTimer(Activity activity, ProjectileModel projectileModel) {
        mActivity = activity;
        mProjectileModel = projectileModel;
    }

    public void start() {
        startTimer();
    }

    public void stop() {
        stopTimer();
    }

    public double getTime() {
        //TODO: return realTime;
        return 0;
    }

    public void updateProjectile(Projectile projectile) {
        TreeMap<Double, LatLng> trajectory = projectile.getTrajectory();
        Double newTimestamp = trajectory.ceilingKey(getTime());

        if (newTimestamp != null) {
            LatLng newPosition = trajectory.get(newTimestamp);
            projectile.setPosition(newPosition);
            //TODO: evaluateContacts
        }
        else {
            projectile.setToDestroy(true);
        }
    }

    private void startTimer() {
        mTimer = new Timer(false);

        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                mLock.lock();
                ArrayList<Projectile> projectiles = mProjectileModel.getProjectiles();
                for (Projectile projectile : projectiles)
                {
                    updateProjectile(projectile);
                }

                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ArrayList<Projectile> projectiles = mProjectileModel.getProjectiles();
                        mGameView.renderProjectiles(projectiles);
                    }
                });
                mLock.unlock();
            }
        }, 0, 100);
    }

    private void stopTimer() {
        if(mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
        }
    }

}
