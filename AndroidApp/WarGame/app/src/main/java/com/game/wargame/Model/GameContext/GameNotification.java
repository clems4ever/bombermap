package com.game.wargame.Model.GameContext;

/**
 * Created by sergei on 02/04/16.
 */
public class GameNotification implements Comparable {
    public static final int TIME_TO_DISPLAY = 200;

    private double mStartTime;
    private String mText;

    public GameNotification(String text, double timeStart) {
        mStartTime = timeStart;
        mText = text;
    }

    public double getStartTime () {
        return mStartTime;
    }

    public String getText() {
        return mText;
    }

    @Override
    public int compareTo(Object another) {
        return (int)(mStartTime-((GameNotification)another).getStartTime());
    }
}
