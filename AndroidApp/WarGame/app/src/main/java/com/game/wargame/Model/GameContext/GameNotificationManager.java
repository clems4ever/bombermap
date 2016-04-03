package com.game.wargame.Model.GameContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by sergei on 02/04/16.
 */
public class GameNotificationManager {
    private ArrayList<GameNotification> mGameNotifications = new ArrayList<>();
    private Lock mLock = new ReentrantLock();

    private static final int NOTIFICATIONS_BUFFER_LENGTH = 3;

    public void pushNotification(GameNotification gameNotification) {
        mGameNotifications.add(gameNotification);
        Collections.sort(mGameNotifications);
    }

    public void getNotificationsToDisplay(ArrayList<GameNotification> gameNotifications) {
        for (int i = 0; i< Math.min(NOTIFICATIONS_BUFFER_LENGTH, mGameNotifications.size()); i++) {
            gameNotifications.add(mGameNotifications.get(i));
        }
    }

    public void removeUnusedNotifications(double time) {
        ArrayList<GameNotification> buffer = new ArrayList<>();
        for (GameNotification gn : mGameNotifications) {
            if (time - gn.getStartTime() < gn.TIME_TO_DISPLAY) {
                buffer.add(gn);
            }
        }
        mGameNotifications = buffer;
    }
}
