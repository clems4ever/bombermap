package com.game.wargame.Entities;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by clement on 09/02/16.
 */
public class Player {


    private String mPlayerId;
    private String mPlayerName;
    private int mHealth = 100;

    private LatLng mPosition;
    private float mRotation;

    public Player(String playerId, String playerName) {
        mPlayerId = playerId;
        mPlayerName = playerName;

        mPosition = new LatLng(0, 0);
        mRotation = 0;
    }

    public void setPlayerId(String playerId) {
        mPlayerId = playerId;
    }

    public String getPlayerId() {
        return mPlayerId;
    }

    public void setPlayerName(String playerName) {
        mPlayerName = playerName;
    }

    public String getPlayerName() {
        return mPlayerName;
    }

    public void setHealth(int health) {
        mHealth = health;
    }

    public int getHealth() {
        return mHealth;
    }

    public void setPosition(LatLng position) {
        mPosition = position;
    }

    public LatLng getPosition() {
        return mPosition;
    }

    public void setRotation(float rotation) {
        mRotation = rotation;
    }

    public float getRotation() {
        return mRotation;
    }

}
