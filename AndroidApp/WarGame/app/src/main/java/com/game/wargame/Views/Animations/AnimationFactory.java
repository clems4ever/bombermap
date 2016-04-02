package com.game.wargame.Views.Animations;

import java.util.ArrayList;

/**
 * Created by sergei on 14/03/16.
 */
public class AnimationFactory implements IAnimationFactory{
    public static Animation buildProjectileAnimation() {
        ProjectileAnimation projectileAnimation = new ProjectileAnimation();
        projectileAnimation.first();
        return projectileAnimation;
    }

    public static Animation buildExplosionAnimation() {
        ExplosionAnimation explosionAnimation = new ExplosionAnimation();
        explosionAnimation.first();
        return explosionAnimation;
    }

    public ArrayList<Animation> buildAllAnimations() {
        ArrayList<Animation> animations = new ArrayList<>();
        animations.add(buildProjectileAnimation());
        animations.add(buildExplosionAnimation());
        return animations;
    }

}
