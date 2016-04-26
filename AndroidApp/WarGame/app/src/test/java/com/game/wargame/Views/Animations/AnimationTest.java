package com.game.wargame.Views.Animations;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by sergei on 20/03/16.
 */
public class AnimationTest {


    public class DummyAnimation extends Animation {
        public DummyAnimation() {
            mDrawablesId.add(1);
            mDrawablesId.add(2);
            mDrawablesId.add(3);
            mFrameRateMillis = 50;
        }

        @Override
        public Size getSize() {
            return new Size(60,60);
        }
    }

    public Animation initDummyAnimation() {
        DummyAnimation animation = new DummyAnimation();
        animation.first();
        return animation;
    }

    @Test
    public void testThatAnimationStartsWithFirstFrame() {
        Animation animation = initDummyAnimation();
        assertEquals(animation.current(), 1);
    }

    @Test
    public void testThatAnimationFrameIsNotUpdatedWithSmallTick() {
        Animation animation = initDummyAnimation();
        animation.addTime(40);
        assertEquals(1, animation.current());
    }

    @Test
    public void testThatAnimationFrameIsUpdated() {
        Animation animation = initDummyAnimation();
        animation.addTime(51);
        assertEquals(2, animation.current());
    }

    @Test
    public void testThatAnimationLoops() {
        Animation animation = initDummyAnimation();
        animation.addTime(50);
        animation.addTime(50);
        animation.addTime(50);
        assertEquals(1, animation.current());
    }
}
