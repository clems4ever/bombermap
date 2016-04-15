package com.game.wargame.Views.Animations;

import com.game.wargame.R;

/**
 * Created by sergei on 05/04/16.
 */
public class PlayerAliveAnimation extends Animation {

    public PlayerAliveAnimation(boolean isRemote) {
        if (isRemote)
            mDrawablesId.add(R.mipmap.profile_c);
        else
            mDrawablesId.add(R.mipmap.profile_s);
    }
}
