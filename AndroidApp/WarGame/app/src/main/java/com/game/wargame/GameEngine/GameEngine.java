package com.game.wargame.GameEngine;

import android.content.Context;
import android.graphics.Point;
import android.location.Location;
import android.util.Log;

import com.game.wargame.Communication.GameEngineSocket;
import com.game.wargame.Entities.LocalPlayerModel;
import com.game.wargame.Entities.OnPlayerPositionChangedListener;
import com.game.wargame.Entities.OnPlayerWeaponTriggeredListener;
import com.game.wargame.Entities.Player;
import com.game.wargame.Entities.PlayerModel;
import com.game.wargame.Sensors.LocationRetriever;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class GameEngine implements OnPlayerPositionChangedListener, OnPlayerWeaponTriggeredListener {

    private static final int WEAPON_TIME = 10000;

    private Context mContext;
    private List<PlayerModel> mPlayers;
    private LocalPlayerModel mCurrentPlayer;

    private GameView mGameView;

    //private Compass mCompass;
    private LocationRetriever mLocationRetriever;

    private GameEngineSocket mGameEngineSocket;

    /**
     * @brief Constructor
     * @param context
     */
    public GameEngine(Context context, GameEngineSocket gameEngineSocket) {
        mContext = context;
        mGameEngineSocket = gameEngineSocket;

        //mCompass = new Compass(mContext);
        mLocationRetriever = new LocationRetriever(mContext);

        mPlayers = new ArrayList<>();
    }

    /**
     * @brief Starts the game engine
     */
    public void start(GameView gameView, LocalPlayerModel localPlayerModel) {
        mGameView = gameView;
        mCurrentPlayer = localPlayerModel;
        addPlayer(mCurrentPlayer);
        initializeView();
    }

    /**
     * @brief Stops the game engine
     */
    public void stop() {
        stopSensors();
    }

    /**
     * @brief Starts the sensors and listen to events
     */
    private void startSensors() {
        //mCompass.start(this);
        mLocationRetriever.start(mCurrentPlayer);
    }

    /**
     * @brief Stops listening to the sensors
     */
    private void stopSensors() {
        //mCompass.stop();
        mLocationRetriever.stop();
    }

    /**
     * @brief Initialize the view
     */
    private void initializeView() {
        mGameView.setOnWeaponTargetDefinedListener(new GameView.OnWeaponTargetDefinedListener() {
            @Override
            public void onWeaponTargetDefined(float x, float y) {
                Point targetPositionInScreenCoordinates = new Point();
                targetPositionInScreenCoordinates.set((int) x, (int) y);

                Projection projection = mGameView.getMapProjection();
                LatLng currentPlayerPosition = mCurrentPlayer.getPosition();
                LatLng targetPosition = projection.fromScreenLocation(targetPositionInScreenCoordinates);

                float[] results = new float[1];
                Location.distanceBetween(currentPlayerPosition.latitude, currentPlayerPosition.longitude, targetPosition.latitude, targetPosition.longitude, results);

                float distanceInMeters = results[0];
                Log.d("Distance in meters", "D=" + String.valueOf(distanceInMeters));

                mCurrentPlayer.fire(targetPosition.latitude, targetPosition.longitude, WEAPON_TIME);
            }
        });

        mGameView.setOnMapReadyListener(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                startSensors();

                Iterator<PlayerModel> playerIt = mPlayers.iterator();
                while (playerIt.hasNext()) {
                    PlayerModel player = playerIt.next();
                    mGameView.addPlayer(player);
                }
            }
        });
    }

    /**
     * @brief Adding a player to the game
     * @param player
     */
    public void addPlayer(PlayerModel player) {
        player.setOnPlayerPositionChangedListener(this);
        player.setOnPlayerWeaponTriggeredListener(this);
        mPlayers.add(player);
    }

    @Override
    public void onPlayerPositionChanged(PlayerModel player) {
        mGameView.movePlayer(player);
    }

    @Override
    public void onPlayerWeaponTriggeredListener(PlayerModel player, double latitude, double longitude, double speed) {
        LatLng source = player.getPosition();
        LatLng destination = new LatLng(latitude, longitude);

        mGameView.triggerWeapon(source, destination, speed);
    }
}
