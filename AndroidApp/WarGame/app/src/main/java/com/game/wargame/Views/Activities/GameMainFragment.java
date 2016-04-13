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
import com.game.wargame.Controller.Settings.Settings;
import com.game.wargame.Model.Entities.VirtualMap.Map;
import com.game.wargame.Model.Entities.VirtualMap.RealMap;
import com.game.wargame.Model.Entities.VirtualMap.Repository;
import com.game.wargame.Model.GameContext.GameContext;
import com.game.wargame.R;
import com.game.wargame.Views.BitmapDescriptorFactory;
import com.game.wargame.Views.BundleExtractor;
import com.game.wargame.Views.GameView;
import com.game.wargame.Views.GoogleMapViewFactory;
import com.game.wargame.Views.IGoogleMapView;
import com.game.wargame.Views.MapView;

public class GameMainFragment extends Fragment {

    private IConnectionManager mConnectionManager;
    private GameEngine mGameEngine;
    private AbstractLocationRetriever mLocationRetriever;

    private String mGameId;
    private String mPlayerId;

    private GameView mGameView;
    private Repository mVirtualMapRepository;

    private GoogleMapViewFactory mGoogleMapViewFactory;
    private BitmapDescriptorFactory mBitmapDescriptorFactory;
    private BundleExtractor mBundleExtractor;

    private Settings mSettings;

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

    private Callback mGameCallback;

    public void setConnectionManager(IConnectionManager connectionManager) {
        mConnectionManager = connectionManager;
    }

    public void setSettings(Settings settings) {
        mSettings = settings;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.game_map, container, false);
        mVirtualMapRepository = new Repository();

        Bundle args = mBundleExtractor.getBundle();
        mGameId = args.getString("game_id");
        mPlayerId = args.getString("player_id");
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();

        View fragment = getView();
        IGoogleMapView googleMapView = mGoogleMapViewFactory.create(fragment);
        mGameView = new GameView((FragmentActivity) getActivity(), fragment, googleMapView, mBitmapDescriptorFactory);


        final GameSocket gameSocket = mConnectionManager.getSocketFactory().buildGameSocket(mGameId);
        final LocalPlayerSocket localPlayerSocket = mConnectionManager.getSocketFactory().buildLocalPlayerSocket(mGameId, mPlayerId);

        if(mSettings.mode == Settings.GameEngineMode.SCENARIO_REPLAYER) {
            mLocationRetriever = new PathPlayer(AppConstant.PLAYER_SCENARIO, false, true);
        }
        else {
            mLocationRetriever = new LocationRetriever(getActivity());
        }

        // Get map 0
        Map virtualMap = mVirtualMapRepository.get(0);

        final RealMap realMap = new RealMap(virtualMap, AppConstant.LAFOURCHE_LATLNG, 1000, 1000, 0);
        mGameEngine = new GameEngine();
        mGameEngine.setCallback(mGameCallback);

        mGameView.start(new MapView.OnMapReadyListener() {
            @Override
            public void onMapReady() {
                mGameEngine.onStart(mGameView,
                        gameSocket,
                        realMap,
                        localPlayerSocket,
                        mLocationRetriever,
                        new GlobalTimer(getActivity()),
                        mSettings
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

    public void setCallback(Callback callback) {
        mGameCallback = callback;
    }

    public interface Callback {
        public void onGameFinish(GameContext gameContext);
        public void onGamePaused(GameContext gameContext);
    }
}
