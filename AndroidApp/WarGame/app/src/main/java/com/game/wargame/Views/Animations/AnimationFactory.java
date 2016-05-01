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
        animations.add(buildPlayerDeadAnimation(1));
        animations.add(buildPlayerDeadAnimation(2));
        animations.add(buildPlayerDeadAnimation(3));
        animations.add(buildPlayerDeadAnimation(4));
        animations.add(buildPlayerShieldedAnimation(1));
        animations.add(buildPlayerShieldedAnimation(2));
        animations.add(buildPlayerShieldedAnimation(3));
        animations.add(buildPlayerShieldedAnimation(4));
        return animations;
    }

    public static Animation buildPlayerDeadAnimation(int isRemote) {
        PlayerDeadAnimation playerDeadAnimation = new PlayerDeadAnimation(isRemote);
        playerDeadAnimation.first();
        return playerDeadAnimation;
    }

    public static Animation buildPlayerShieldedAnimation(int isRemote) {
        PlayerShieldedAnimation playerShieldedAnimation = new PlayerShieldedAnimation(isRemote);
        playerShieldedAnimation.first();
        return playerShieldedAnimation;
    }

    public static Animation buildPlayerAliveAnimation(int isRemote) {
        PlayerAliveAnimation playerAliveAnimation = new PlayerAliveAnimation(isRemote);
        playerAliveAnimation.first();
        return playerAliveAnimation;
    }
}
