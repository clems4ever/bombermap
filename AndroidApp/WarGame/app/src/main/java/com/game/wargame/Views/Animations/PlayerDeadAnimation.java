package com.game.wargame.Views.Animations;

import com.game.wargame.R;

/**
 * Created by sergei on 05/04/16.
 */
public class PlayerDeadAnimation extends Animation{
    public PlayerDeadAnimation(boolean isRemote) {
        if (isRemote)
            mDrawablesId.add(R.mipmap.player);
        else
            mDrawablesId.add(R.mipmap.player_current);
        mDrawablesId.add(R.mipmap.void_image);

        mFrameRateMillis = 300;
    }
}
