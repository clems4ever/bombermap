package com.game.wargame.Views.Activities;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.game.wargame.Controller.Communication.ConnectionManager;
import com.game.wargame.Controller.GameEngine;
import com.game.wargame.Controller.Sensors.LocationRetriever;
import com.game.wargame.Model.Entities.LocalPlayerModel;
import com.game.wargame.R;
import com.game.wargame.Views.GameView;
import com.game.wargame.WarGameApplication;

public class GameActivity extends FragmentActivity {

    private Context mContext;
    private WarGameApplication mApplication;

    private GameEngine mGameEngine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mContext = this;

        mApplication = (WarGameApplication) mContext.getApplicationContext();
    }

    @Override
    protected void onStart() {
        super.onStart();
        ConnectionManager.onStart();

        // Get the current user
        Intent myIntent = getIntent();
        String username = myIntent.getStringExtra("username");

        mGameEngine = new GameEngine();

        LocalPlayerModel localPlayer = new LocalPlayerModel(username, mApplication.mLocalPlayerSocket);
        mGameEngine.onStart(new GameView(this), mApplication.mGameSocket, localPlayer, new LocationRetriever(mContext));
    }


    @Override
    protected void onStop() {
        mGameEngine.onStop();
        ConnectionManager.onStop();
        super.onStop();
    }
}
