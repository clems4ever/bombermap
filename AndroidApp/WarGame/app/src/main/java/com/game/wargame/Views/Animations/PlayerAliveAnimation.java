package com.game.wargame.Views.Animations;

import com.game.wargame.R;

/**
 * Created by sergei on 05/04/16.
 */
public class PlayerAliveAnimation extends Animation {

    public static final Size SIZE = new Size(64, 64);

    public PlayerAliveAnimation(boolean isRemote) {
        if (isRemote)
            mDrawablesId.add(R.mipmap.profile_c);
        else
            mDrawablesId.add(R.mipmap.profile_s);
    }

    public static int getResourceIdForNumero(int numero) {
        switch(numero) {
            case 1:
                return R.mipmap.profile_c;
            case 2:
                return R.mipmap.profile_s;
            case 3:
                return R.mipmap.player;
            case 4:
                return R.mipmap.marker_current;
        }
        return 0;
    }

    public PlayerAliveAnimation(int numero) {
          mDrawablesId.add(getResourceIdForNumero(numero));
    }

    @Override
    public Size getSize() {
        return SIZE;
    }
}
