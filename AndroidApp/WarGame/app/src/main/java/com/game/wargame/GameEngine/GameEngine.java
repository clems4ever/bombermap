package com.game.wargame.GameEngine;

import android.content.Context;
import android.location.Location;
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
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;


public class GameEngine implements LocationListener, Compass.OnCompassChangedListener  {

    private Context mContext;
    private List<Player> mPlayers;
    private Player mCurrentPlayer;

    private GameView mGameView;

    private Compass mCompass;
    private LocationRetriever mLocationRetriever;

    private RemoteCommunicationSystem mRemoteCommunicationSystem;

    /**
     * @brief Constructor
     * @param context
     * @param gameView
     */
    public GameEngine(Context context, GameView gameView, RemoteCommunicationSystem remoteCommunicationSystem) {
        mContext = context;
        mGameView = gameView;
        mRemoteCommunicationSystem = remoteCommunicationSystem;

        mCompass = new Compass(mContext);
        mLocationRetriever = new LocationRetriever(mContext);

        mPlayers = new ArrayList<>();
    }

    /**
     * @brief Starts the game engine
     */
    public void start() {
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
        mCompass.start(this);
        mLocationRetriever.start(this);
    }

    /**
     * @brief Stops listening to the sensors
     */
    private void stopSensors() {
        mCompass.stop();
        mLocationRetriever.stop();
    }

    /**
     * @brief Create the current user
     */
    private void createCurrentPlayer() {
        mCurrentPlayer = new Player(0, "CURRENT PLAYER");
        addPlayer(mCurrentPlayer);
    }

    /**
     * @brief Initialize the view
     */
    private void initializeView() {
        mGameView.setFireButtonClickedListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO, the user clicked on fire
                // Trigger fire event
            }
        });

        mGameView.setOnMapReadyListener(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                startSensors();
                createCurrentPlayer();
            }
        });
    }

    /**
     * @brief Adding a player to the game
     * @param player
     */
    public void addPlayer(Player player) {
        mPlayers.add(player);
        mGameView.addPlayer(player);
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

    @Override
    public void onCompassChanged(float yaw, float roll, float pitch) {
        mCurrentPlayer.setRotation(yaw);
        mGameView.rotatePlayer(mCurrentPlayer);

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
