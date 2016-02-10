package com.game.wargame;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.game.wargame.GameEngine.GameEngine;
import com.game.wargame.GameEngine.GameView;
import com.game.wargame.R;
import com.google.android.gms.maps.SupportMapFragment;

public class MapActivity extends FragmentActivity {

    private GameEngine mGameEngine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        GameView mGameGameView = new GameView(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(mGameGameView);

        mGameEngine = new GameEngine(this, mGameGameView);
        mGameEngine.start();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    protected void onStop() {
        mGameEngine.stop();
        super.onStop();
    }
}
