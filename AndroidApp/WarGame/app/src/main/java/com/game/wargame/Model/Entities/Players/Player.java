package com.game.wargame.Model.Entities.Players;

import com.game.wargame.AppConstant;
import com.game.wargame.Controller.Engine.DisplayTransaction;
import com.game.wargame.Model.Entities.EntitiesContainerUpdater;
import com.game.wargame.Model.Entities.Entity;
import com.game.wargame.Views.Animations.Animation;
import com.google.android.gms.maps.model.LatLng;


public class Player extends Entity {

    protected final static int TIME_TO_RESPAWN = 6000;

    protected String mPlayerId;
    protected String mPlayerName;
    protected int mHealth = 100;
    protected boolean mIsVisible = true;
    protected int mRespawnCounter = 0;

    protected boolean mIsShielded;

    public boolean isShielded() {
        return mIsShielded;
    }

    public void setShielded(boolean isShielded) {
        mIsShielded = isShielded;
    }

    public void shield() {
        setShielded(!isShielded());
    }

    public Player(String playerId, String playerName) {
        mPlayerId = playerId;
        mPlayerName = playerName;

        mPosition = new LatLng(AppConstant.INITIAL_LATITUDE, AppConstant.INITIAL_LONGITUDE);
    }

    @Override
    public boolean equals(Object obj) {
        Player that = (Player) obj;
        return mPlayerId == that.mPlayerId;
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

    public void setHealth(int health) throws PlayerException {
        if(health < 0 || health > 100) {
            throw new PlayerException("Health out of bound");
        }
        mHealth = health;
    }

    public int getHealth() {
        return mHealth;
    }

    @Override
    public void update(long ticks, int increment, EntitiesContainerUpdater entitiesContainerUpdater, DisplayTransaction displayTransaction) {
        super.update(ticks, increment, entitiesContainerUpdater, displayTransaction);
    }

    public Animation getAnimation() {
        return mAnimation;
    }
}
