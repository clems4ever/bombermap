package com.game.wargame.Views.Activities;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.game.wargame.Controller.Communication.Game.GameManagerSocket;
import com.game.wargame.Controller.Communication.Game.GameSocket;
import com.game.wargame.Controller.Communication.Game.LocalPlayerSocket;
import com.game.wargame.Controller.Communication.IConnectionManager;
import com.game.wargame.Controller.Engine.GlobalTimer;
import com.game.wargame.Controller.GameEngine;
import com.game.wargame.Controller.Sensors.LocationRetriever;
import com.game.wargame.Model.Entities.VirtualMap.Map;
import com.game.wargame.Model.Entities.VirtualMap.Repository;
import com.game.wargame.R;
import com.game.wargame.Views.BitmapDescriptorFactory;
import com.game.wargame.Views.BundleExtractor;
import com.game.wargame.Views.GameView;
import com.game.wargame.Views.GoogleMapViewFactory;
import com.game.wargame.Views.IGoogleMapView;
import com.game.wargame.Views.MapView;

import org.junit.Test;

public class GameMainFragment extends Fragment {

    private IConnectionManager mConnectionManager;
    private GameEngine mGameEngine;

    private String mGameId;
    private String mPlayerId;

    private GameView mGameView;
    private Repository mVirtualMapRepository;

    private GoogleMapViewFactory mGoogleMapViewFactory;
    private BitmapDescriptorFactory mBitmapDescriptorFactory;
    private BundleExtractor mBundleExtractor;



    public GameMainFragment() {
        mGoogleMapViewFactory = new GoogleMapViewFactory();
        mBitmapDescriptorFactory = new BitmapDescriptorFactory();
        mBundleExtractor = new BundleExtractor(this);
    }

    // TEST
    public GameMainFragment(GoogleMapViewFactory googleMapViewFactory, BitmapDescriptorFactory bitmapDescriptorFactory, BundleExtractor bundleExtractor) {
        mGoogleMapViewFactory = googleMapViewFactory;
        mBitmapDescriptorFactory = bitmapDescriptorFactory;
        mBundleExtractor = bundleExtractor;
    }

    public void setConnectionManager(IConnectionManager connectionManager) {
        mConnectionManager = connectionManager;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.game_map, container, false);
        IGoogleMapView googleMapView = mGoogleMapViewFactory.create(fragment);

        mGameView = new GameView((FragmentActivity) getActivity(), fragment, googleMapView, mBitmapDescriptorFactory);
        mVirtualMapRepository = new Repository();

        Bundle args = mBundleExtractor.getBundle();
        mGameId = args.getString("game_id");
        mPlayerId = args.getString("player_id");
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();

        final GameSocket gameSocket = mConnectionManager.getSocketFactory().buildGameSocket(mGameId);
        final LocalPlayerSocket localPlayerSocket = mConnectionManager.getSocketFactory().buildLocalPlayerSocket(mGameId, mPlayerId);

        // Get map 0
        final Map virtualMap = mVirtualMapRepository.get(0);


        mGameView.start(new MapView.OnMapReadyListener() {
            @Override
            public void onMapReady() {
                mGameEngine = new GameEngine();
                mGameEngine.onStart(mGameView,
                        gameSocket,
                        virtualMap,
                        localPlayerSocket,
                        new LocationRetriever(getActivity()),
                        new GlobalTimer((FragmentActivity) getActivity())
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
