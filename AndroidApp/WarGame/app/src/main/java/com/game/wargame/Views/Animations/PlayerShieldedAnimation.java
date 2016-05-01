package com.game.wargame.Views.Animations;

import com.game.wargame.R;

/**
 * Created by sergei on 16/04/16.
 */
public class PlayerShieldedAnimation extends Animation {

    protected static final Size SIZE = new Size(64, 64);

    public static int getResourceIdForNumero(int numero) {
        switch(numero) {
            case 1:
                return R.mipmap.profile_c_shield;
            case 2:
                return R.mipmap.profile_s_shield;
            case 3:
                return R.mipmap.paulo_shielded;
            case 4:
                return R.mipmap.jacqueline_shielded;
        }
        return 0;
    }

    PlayerShieldedAnimation(int numero) {
        mDrawablesId.add(PlayerShieldedAnimation.getResourceIdForNumero(numero));
    }

    @Override
    public Size getSize() {
        return SIZE;
    }

}
