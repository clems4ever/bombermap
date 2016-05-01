package com.game.wargame.Controller.GameLogic;

/**
 * Created by sergei on 19/03/16.
 */
public class GameScore {

    private int mFrags;
    private int mDeaths;
    private String mPlayerId;

    public GameScore(String playerId) {
        mPlayerId = playerId;
    }

    public String getPlayerId() {
        return mPlayerId;
    }

    public int getFrags() {
        return mFrags;
    }

    public int getDeaths() {
        return mDeaths;
    }

    public void setFrags(int frags) {
        mFrags = frags;
    }

    public void setDeaths(int deaths) {
        mDeaths = deaths;
    }

    public String toString() {
        return mFrags+" "+mDeaths;
    }

}
