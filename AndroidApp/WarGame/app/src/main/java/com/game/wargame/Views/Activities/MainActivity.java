package com.game.wargame.Views.Activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.game.wargame.AppConstant;
import com.game.wargame.Controller.Communication.IConnectionManager;
import com.game.wargame.Controller.Communication.RabbitMQ.RabbitMQConnectionManager;
import com.game.wargame.Controller.Settings.Settings;
import com.game.wargame.Controller.Settings.SettingsReader;
import com.game.wargame.Model.GameContext.GameContext;
import com.game.wargame.R;

import org.json.JSONException;

import java.io.IOException;

public class MainActivity extends FragmentActivity implements GameEntryFragment.Callback, GameMainFragment.Callback {

    private IConnectionManager mConnectionManager;
    private Settings mSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSettings = new Settings();

        SettingsReader settingsReader = new SettingsReader();
        try {
            mSettings = settingsReader.read(Environment.getExternalStorageDirectory().getPath() + "/default.conf");
        } catch(java.io.FileNotFoundException e) {
            Log.d("WarGame settings", "File not found:" + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mConnectionManager = new RabbitMQConnectionManager(mSettings.virtualHost);

        GameEntryFragment gameEntryFragment = new GameEntryFragment();
        gameEntryFragment.setConnectionManager(mConnectionManager);
        gameEntryFragment.setGameEntryCallbacks(this);
        gameEntryFragment.setSettings(mSettings);
        addFragment(gameEntryFragment);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mConnectionManager.connect(mSettings.hostname);
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
        gameFragment.setCallback(this);
        gameFragment.setSettings(mSettings);

        Bundle args = new Bundle();
        args.putString("game_id", gameId);
        args.putString("player_id", playerId);
        gameFragment.setArguments(args);

        replaceFragment(gameFragment);
    }

    @Override
    public void onGameFinish(GameContext gameContext) {
        ScoreBoardFragment scoreBoardFragment = new ScoreBoardFragment();

        scoreBoardFragment.setGameContext(gameContext);

        replaceFragment(scoreBoardFragment);
    }

    @Override
    public void onGamePaused(GameContext gameContext) {

    }
}
