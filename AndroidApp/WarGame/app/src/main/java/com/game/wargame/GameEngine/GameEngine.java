package com.game.wargame.GameEngine;

import android.content.Context;
import android.graphics.Point;
import android.location.Location;
import android.util.Log;
import android.view.View;

import com.game.wargame.Communication.RemoteCommunicationSystem;
import com.game.wargame.Entities.Player;
import com.game.wargame.Sensors.Compass;
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


public class GameEngine implements LocationListener  {

    private static final int WEAPON_TIME = 10000;

    private Context mContext;
    private List<Player> mPlayers;
    private Player mCurrentPlayer;

    private GameView mGameView;

    //private Compass mCompass;
    private LocationRetriever mLocationRetriever;

    private RemoteCommunicationSystem mRemoteCommunicationSystem;

    /**
     * @brief Constructor
     * @param context
     */
    public GameEngine(Context context, RemoteCommunicationSystem remoteCommunicationSystem) {
        mContext = context;
        mRemoteCommunicationSystem = remoteCommunicationSystem;

        //mCompass = new Compass(mContext);
        mLocationRetriever = new LocationRetriever(mContext);

        mPlayers = new ArrayList<>();
    }

    /**
     * @brief Starts the game engine
     */
    public void start(GameView gameView) {
        mGameView = gameView;
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
        mLocationRetriever.start(this);
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
        mGameView.setCurrentPlayerId(mCurrentPlayer.getPlayerId());
        mGameView.setOnWeaponTargetDefinedListener(new GameView.OnWeaponTargetDefinedListener() {
            @Override
            public void onWeaponTargetDefined(float x, float y) {
                Point targetPositionInScreenCoordinates = new Point();
                targetPositionInScreenCoordinates.set((int)x, (int)y);

                Projection projection = mGameView.getMapProjection();
                LatLng currentPlayerPosition = mCurrentPlayer.getPosition();
                LatLng targetPosition = projection.fromScreenLocation(targetPositionInScreenCoordinates);

                float[] results = new float[1];
                Location.distanceBetween(currentPlayerPosition.latitude, currentPlayerPosition.longitude, targetPosition.latitude, targetPosition.longitude, results);

                float distanceInMeters = results[0];
                Log.d("Distance in meters", "D=" + String.valueOf(distanceInMeters));

                mRemoteCommunicationSystem.fire(currentPlayerPosition.latitude, currentPlayerPosition.longitude, targetPosition.latitude, targetPosition.longitude, WEAPON_TIME);
            }
        });

        mGameView.setOnMapReadyListener(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                startSensors();

                Iterator<Player> playerIt = mPlayers.iterator();
                while(playerIt.hasNext()) {
                    Player player = playerIt.next();
                    mGameView.addPlayer(player);
                }
            }
        });
    }

    /**
     * @brief Adding a player to the game
     * @param player
     */
    public void addPlayer(Player player) {
        mPlayers.add(player);
    }

    /**
     * @brief Set current player
     * @param player
     */
    public void setCurrentPlayer(Player player) {
        mCurrentPlayer = player;
    }

    /**
     * @brief Callback called when the GPS location has changed
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {
        mCurrentPlayer.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(mCurrentPlayer.getPosition());
        mGameView.animateCamera(cameraUpdate);
        mGameView.movePlayer(mCurrentPlayer);

        try {
            sendMoveEventToServer();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendMoveEventToServer() throws JSONException {
        mRemoteCommunicationSystem.move(mCurrentPlayer.getPosition().latitude, mCurrentPlayer.getPosition().longitude, mCurrentPlayer.getRotation());
    }
}
