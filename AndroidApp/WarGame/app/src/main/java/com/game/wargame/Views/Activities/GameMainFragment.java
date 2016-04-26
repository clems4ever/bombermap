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
import com.game.wargame.Controller.Sensors.GPSSensorLocationUpdater;
import com.game.wargame.Controller.Sensors.ManualLocationUpdater;
import com.game.wargame.Controller.Sensors.RecordedPathLocationUpdater;
import com.game.wargame.Controller.Settings.Settings;
import com.game.wargame.Model.Entities.VirtualMap.Map;
import com.game.wargame.Model.Entities.VirtualMap.RealMap;
import com.game.wargame.Model.Entities.VirtualMap.Repository;
import com.game.wargame.Model.GameContext.GameContext;
import com.game.wargame.R;
import com.game.wargame.Views.Bitmaps.BitmapDescriptorDescriptorFactory;
import com.game.wargame.Views.BundleExtractor;
import com.game.wargame.Views.Views.GameView;
import com.game.wargame.Views.GoogleMap.GoogleMapViewFactory;
import com.game.wargame.Views.GoogleMap.IGoogleMapView;
import com.game.wargame.Views.Views.MapView;

public class GameMainFragment extends Fragment implements MapView.OnMapReadyListener {

    private IConnectionManager mConnectionManager;
    private GameEngine mGameEngine;
    private AbstractLocationRetriever mLocationRetriever;

    private String mGameId;
    private String mPlayerId;

    private GameView mGameView;
    private Repository mVirtualMapRepository;

    private GoogleMapViewFactory mGoogleMapViewFactory;
    private BitmapDescriptorDescriptorFactory mBitmapDescriptorFactory;
    private BundleExtractor mBundleExtractor;

    private Settings mSettings;

    public GameMainFragment() {
        mGoogleMapViewFactory = new GoogleMapViewFactory();
        mBundleExtractor = new BundleExtractor(this);
    }

    // TEST
    public GameMainFragment(GoogleMapViewFactory googleMapViewFactory, BitmapDescriptorDescriptorFactory bitmapDescriptorFactory, BundleExtractor bundleExtractor, Repository virtualMapRepository) {
        mGoogleMapViewFactory = googleMapViewFactory;
        mBitmapDescriptorFactory = bitmapDescriptorFactory;
        mBundleExtractor = bundleExtractor;
        mVirtualMapRepository = virtualMapRepository;
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

        if(mVirtualMapRepository == null) {
            mVirtualMapRepository = new Repository(getResources());
        }

        Bundle args = mBundleExtractor.getBundle();
        mGameId = args.getString("game_id");
        mPlayerId = args.getString("player_id");
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();

        View fragment = getView();
        mBitmapDescriptorFactory = new BitmapDescriptorDescriptorFactory(getActivity());
        IGoogleMapView googleMapView = mGoogleMapViewFactory.create(fragment);
        mGameView = new GameView((FragmentActivity) getActivity(), fragment, googleMapView, mBitmapDescriptorFactory);

        mGameEngine = new GameEngine();
        mGameEngine.setCallback(mGameCallback);
        mGameView.start(this);
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

    public void onMapReady() {
        final GameSocket gameSocket = mConnectionManager.getSocketFactory().buildGameSocket(mGameId);
        final LocalPlayerSocket localPlayerSocket = mConnectionManager.getSocketFactory().buildLocalPlayerSocket(mGameId, mPlayerId);

        Map virtualMap = mVirtualMapRepository.get(2);

        final RealMap realMap = new RealMap(virtualMap, AppConstant.LAFOURCHE_LATLNG, 20, 20, 0);

        if(mSettings.mode == Settings.GameEngineMode.SCENARIO_REPLAYER) {
            mLocationRetriever = new RecordedPathLocationUpdater(mSettings.playerScenario, false, true);
        }
        else if(mSettings.mode == Settings.GameEngineMode.MANUAL) {
            ManualLocationUpdater manualLocationUpdater = new ManualLocationUpdater();
            mGameView.getMapView().setOnMapLongClickListener(manualLocationUpdater);
            mLocationRetriever = manualLocationUpdater;
        }
        else {
            mLocationRetriever = new GPSSensorLocationUpdater(getActivity());
        }

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

    public void setCallback(Callback callback) {
        mGameCallback = callback;
    }

    public interface Callback {
        public void onGameFinish(GameContext gameContext);
        public void onGamePaused(GameContext gameContext);
    }
}
