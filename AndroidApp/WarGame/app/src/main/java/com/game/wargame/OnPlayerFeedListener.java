package com.game.wargame;

import android.app.Activity;

import com.github.nkzawa.emitter.Emitter;

public class OnPlayerFeedListener implements Emitter.Listener {

    private Activity m_activity;

    OnPlayerFeedListener(Activity activity) {
        m_activity = activity;
    }

    @Override
    public void call(Object... args) {
        m_activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

            }
        });
    }
}
