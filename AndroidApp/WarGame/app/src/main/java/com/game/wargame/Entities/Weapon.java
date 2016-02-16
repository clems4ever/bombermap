package com.game.wargame.Entities;

/**
 * Created by clement on 16/02/16.
 */
public class Weapon {

    private String mName;
    private double mShootRange;
    private double mImpactRange;
    private double mStrength;

    public Weapon(String name) {
        mName = name;

        mShootRange = 100.0f;
        mImpactRange = 100.0f;
        mStrength = 100.0f;
    }

    public void setShootRange(double shootRange) {
        mShootRange = shootRange;
    }

    public double getShootRange() {
        return mShootRange;
    }

    public void setImpactRange(double impactRange) {
        mImpactRange = impactRange;
    }

    public double getImpactRange() {
        return mImpactRange;
    }

    public void setStrength(double strength) {
        mStrength = strength;
    }

    public double getStrength() {
        return mStrength;
    }
}
