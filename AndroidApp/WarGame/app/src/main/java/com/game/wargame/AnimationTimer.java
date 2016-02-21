package com.game.wargame;

import android.app.Activity;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AnimationTimer extends Timer {

    private Activity mActivity;
    private Lock mLock = new ReentrantLock();

    private Timer mTimer;

    private List<BulletAnimation> mBulletAnimations = new LinkedList<>();

    public AnimationTimer(Activity activity) {
        mActivity = activity;
    }

    public void start() {
        startTimer();
    }

    public void stop() {
        stopTimer();
    }

    private void startTimer() {
        mTimer = new Timer(false);

        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                mLock.lock();
                final Iterator<BulletAnimation> animationIterator = mBulletAnimations.iterator();

                while (animationIterator.hasNext()) {
                    final BulletAnimation bulletAnimation = animationIterator.next();
                    boolean lastEvent = false;

                    if (bulletAnimation.hasNext()) {
                        bulletAnimation.next();
                        lastEvent = !bulletAnimation.hasNext();
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                bulletAnimation.onDraw();
                                if(!bulletAnimation.hasNext()) {
                                    bulletAnimation.onEnd();
                                }
                            }
                        });
                    }
                    if(lastEvent) {
                        animationIterator.remove();
                    }
                }
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

    public void startBulletAnimation(final BulletAnimation bulletAnimation) {
        mLock.lock();
        mBulletAnimations.add(bulletAnimation);
        mLock.unlock();

        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bulletAnimation.onStart();
            }
        });

    }
}
