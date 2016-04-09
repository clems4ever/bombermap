package com.game.wargame.Views.Activities;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.game.wargame.AppConstant;
import com.game.wargame.Controller.Communication.Game.GameManagerSocket;
import com.game.wargame.Controller.Communication.Game.GameSocket;
import com.game.wargame.Controller.Communication.Game.LocalPlayerSocket;
import com.game.wargame.Controller.Communication.IConnectionManager;
import com.game.wargame.Controller.Engine.GlobalTimer;
import com.game.wargame.Controller.GameEngine;
import com.game.wargame.Controller.Sensors.AbstractLocationRetriever;
import com.game.wargame.Controller.Sensors.LocationRetriever;
import com.game.wargame.Controller.Sensors.PathPlayer;
import com.game.wargame.R;
import com.game.wargame.Views.GameView;
import com.game.wargame.Views.MapView;

public class GameMainFragment extends Fragment {

    private IConnectionManager mConnectionManager;
    private GameEngine mGameEngine;

    private String mGameId;
    private String mPlayerId;

    private GameView mGameView;

    public void setConnectionManager(IConnectionManager connectionManager) {
        mConnectionManager = connectionManager;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.game_map, container, false);
        mGameView = new GameView((FragmentActivity) getActivity(), fragment);

        Bundle args = getArguments();
        mGameId = args.getString("game_id");
        mPlayerId = args.getString("player_id");
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();

        final GameSocket gameSocket = mConnectionManager.getSocketFactory().buildGameSocket(mGameId);
        final LocalPlayerSocket localPlayerSocket = mConnectionManager.getSocketFactory().buildLocalPlayerSocket(mGameId, mPlayerId);

        final AbstractLocationRetriever locationRetriever = new PathPlayer(AppConstant.PLAYER_SCENARIO, false, true);

        mGameView.start(new MapView.OnMapReadyListener() {
            @Override
            public void onMapReady() {
                mGameEngine = new GameEngine();
                mGameEngine.onStart(mGameView,
                        gameSocket,
                        localPlayerSocket,
                        locationRetriever,
                        new GlobalTimer(getActivity())
                );

                // Unfreeze messages when view is loaded
                mConnectionManager.unfreeze();
            }
        });
    }

    @Override
    public void onStop() {
        if(mGameEngine != null) {
            mGameEngine.onStop();

            GameManagerSocket gameManagerSocket = mConnectionManager.getSocketFactory().buildGameManagerSocket();
            gameManagerSocket.leaveGame(mGameId, mPlayerId);
        }

        mConnectionManager.clear();

        super.onStop();
    }
}
