package com.game.wargame.WeaponControllers;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.game.wargame.R;

public class RocketControllerView extends AbstractWeaponControllerView {

    public RocketControllerView(Context context) {
        super(context);
    }

    public RocketControllerView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public RocketControllerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void initialize() {

        inflate(getContext(), R.layout.drone_controller, this);

    }

    @Override
    public void finalize() {

    }
}
