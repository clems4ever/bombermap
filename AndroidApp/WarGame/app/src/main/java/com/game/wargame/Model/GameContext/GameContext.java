package com.game.wargame.Model.GameContext;

import com.game.wargame.Controller.GameLogic.GameScore;
import com.game.wargame.Model.Entities.Players.Player;
import com.game.wargame.Model.Entities.Updatable;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by sergei on 18/03/16.
 */
public class GameContext implements Updatable {

    private boolean mStarted;
    private double mTimeStart = -1;
    private double mCurrentTime;
    private int mGameDurationMilliseconds;
    private FragManager mFragManager;
    private GameNotificationManager mGameNotificationManager;

    public GameContext(FragManager fragManager, GameNotificationManager gameNotificationManager, int gameDurationSeconds) {
        mFragManager = fragManager;
        mGameNotificationManager = gameNotificationManager;
        mGameDurationMilliseconds = gameDurationSeconds * 1000;
    }

    public void setTimeStart(double timeStart) {
        mTimeStart = timeStart;
    }

    public double getTimeStart() {
        return mTimeStart;
    }

    public boolean isStarted() { return mStarted; }

    public boolean toEnd() {
        return (mCurrentTime - mTimeStart) > mGameDurationMilliseconds;
    }

    public void update(long ticks, int increment) {
        if (isStarted() && !toEnd()) {
            mCurrentTime = ticks * increment;
            purgeNotifications(mCurrentTime);
        }
    }

    public int getRemainingTime() {
        return (int)(mGameDurationMilliseconds -(mCurrentTime-mTimeStart))/1000;
    }

    public void addPlayer(String playerId) {
        mFragManager.addPlayer(playerId);
    }

    public void handleFrag(Player dead, Player killer, double time) {
        mFragManager.addFrag(killer.getPlayerId());
        mFragManager.addDeath(dead.getPlayerId());
        GameNotification gameNotification = new GameNotification(killer.getPlayerName()+" killed "+dead.getPlayerName()+" savagely", time);
        mGameNotificationManager.pushNotification(gameNotification);
    }

    public ArrayList<GameNotification> getNotificationsToDisplay() {
        ArrayList<GameNotification> gameNotifications = new ArrayList<>();
        mGameNotificationManager.getNotificationsToDisplay(gameNotifications);
        return gameNotifications;
    }

    public Map<String, GameScore> getScores() {
        return mFragManager.getScores();
    }

    public void purgeNotifications(double time) {
        mGameNotificationManager.removeUnusedNotifications(time);
    }

    public void start(double time) {
        mTimeStart = time;
        mStarted = true;
    }
}
