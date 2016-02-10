package com.game.wargame;

/**
 * Created by clement on 09/02/16.
 */
public class Player {

    private int mHealth = 100;

    public Player(String playerName) {

    }

    public void setHealth(int health) {
        mHealth = health;
    }

    public int getHealth() {
        return mHealth;
    }

}
