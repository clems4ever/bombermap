package com.game.wargame.Model.GameContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by sergei on 02/04/16.
 */
public class GameNotificationManager {
    private ArrayList<GameNotification> mGameNotifications;
    private ReentrantLock mLock;

    private static final int NOTIFICATIONS_BUFFER_LENGTH = 3;

    public void pushNotification(GameNotification gameNotification) {
        mLock.lock();
        mGameNotifications.add(gameNotification);
        Collections.sort(mGameNotifications);
        mLock.unlock();
    }

    public void getNotificationsToDisplay(GameNotification[] gameNotifications, double time) {
        mLock.lock();
        if (gameNotifications.length == NOTIFICATIONS_BUFFER_LENGTH) {
            for (int i = 0; i< Math.min(NOTIFICATIONS_BUFFER_LENGTH, mGameNotifications.size()); i++) {
                gameNotifications[i] = mGameNotifications.get(i);
            }
        }
        mLock.unlock();
    }

    public void removeUnusedNotifications(double time) {
        ArrayList<GameNotification> buffer = new ArrayList<>();
        mLock.lock();
        for (GameNotification gn : mGameNotifications) {
            if (time - gn.getStartTime() < gn.TIME_TO_DISPLAY) {
                buffer.add(gn);
            }
        }
        mGameNotifications = buffer;
        mLock.unlock();
    }
}
