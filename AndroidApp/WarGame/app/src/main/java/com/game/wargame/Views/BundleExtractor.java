package com.game.wargame.Views;

import android.app.Fragment;
import android.os.Bundle;

/**
 * Created by clement on 03/04/16.
 */
public class BundleExtractor {

    private Fragment mFragment;

    public BundleExtractor(Fragment fragment) {
        mFragment = fragment;
    }

    public Bundle getBundle() {
        return mFragment.getArguments();
    }
}
