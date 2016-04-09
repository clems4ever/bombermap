package com.game.wargame.Model.GameContext;

import com.game.wargame.AppConstant;
import com.game.wargame.Controller.GameLogic.GameScore;
import com.game.wargame.Model.Entities.Players.Player;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by sergei on 18/03/16.
 */
public class GameContext {

    private static final int GAME_DURATION = AppConstant.GAME_DURATION * 1000;

    private boolean mStarted;
    private double mTimeStart;
    private double mCurrentTime;
    private FragManager mFragManager;
    private GameNotificationManager mGameNotificationManager;

    public GameContext(FragManager fragManager, GameNotificationManager gameNotificationManager) {
        mFragManager = fragManager;
        mGameNotificationManager = gameNotificationManager;
    }

    public void setTimeStart(int timeStart) {
        mTimeStart = timeStart;
    }

    public void setCurrentTime(int currentTime) {
        mCurrentTime = currentTime;
    }

    public boolean isStarted() { return mStarted; }

    public boolean toEnd() {
        return (mCurrentTime - mTimeStart) > GAME_DURATION;
    }

    public void update(long ticks, int increment) {
        if (isStarted() && !toEnd()) {
            mCurrentTime = ticks * increment;
            purgeNotifications(mCurrentTime);
        }
    }

    public int getRemainingTime() {
        return (int)(GAME_DURATION-(mCurrentTime-mTimeStart))/1000;
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
