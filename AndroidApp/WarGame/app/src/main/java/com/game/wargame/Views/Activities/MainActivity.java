package com.game.wargame.Views.Activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.game.wargame.AppConstant;
import com.game.wargame.Controller.Communication.IConnectionManager;
import com.game.wargame.Controller.Communication.RabbitMQ.RabbitMQConnectionManager;
import com.game.wargame.R;

public class MainActivity extends FragmentActivity implements GameEntryFragment.Callback {

    private IConnectionManager mConnectionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mConnectionManager = new RabbitMQConnectionManager(AppConstant.VIRTUAL_HOST);

        GameEntryFragment gameEntryFragment = new GameEntryFragment();
        gameEntryFragment.setConnectionManager(mConnectionManager);
        gameEntryFragment.setGameEntryCallbacks(this);
        addFragment(gameEntryFragment);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mConnectionManager.connect(AppConstant.HOST);
    }

    @Override
    protected void onStop() {
        mConnectionManager.disconnect();

        super.onStop();
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0 ){
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }


    private void addFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(android.R.id.content, fragment);
        fragmentTransaction.commit();
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(android.R.id.content, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onGameCreated(String gameId) {
    }

    @Override
    public void onPlayerJoined(String gameId, String playerId) {
        GameMainFragment gameFragment = new GameMainFragment();
        mConnectionManager.initSocketFactory();
        gameFragment.setConnectionManager(mConnectionManager);

        Bundle args = new Bundle();
        args.putString("game_id", gameId);
        args.putString("player_id", playerId);
        gameFragment.setArguments(args);

        replaceFragment(gameFragment);
    }
}
