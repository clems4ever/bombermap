package com.game.wargame.Views.Animations;

import com.game.wargame.R;

/**
 * Created by sergei on 16/04/16.
 */
public class PlayerShieldedAnimation extends Animation {

    protected static final Size SIZE = new Size(64, 64);

    PlayerShieldedAnimation(boolean remote) {
        if (remote)
            mDrawablesId.add(R.mipmap.profile_c_shield);
        else
            mDrawablesId.add(R.mipmap.profile_s_shield);
    }

    @Override
    public Size getSize() {
        return SIZE;
    }

}
