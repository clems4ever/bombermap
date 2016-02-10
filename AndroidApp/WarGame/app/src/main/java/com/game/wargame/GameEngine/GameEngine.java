package com.game.wargame.GameEngine;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.view.View;

import com.game.wargame.Sensors.Compass;
import com.game.wargame.Sensors.LocationRetriever;
import com.game.wargame.Entities.Player;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;


public class GameEngine implements LocationListener, Compass.OnCompassChangedListener  {

    private final String SERVER_URI = "http://10.0.2.2:3001";

    private Context mContext;
    private List<Player> mPlayers;
    private Player mCurrentPlayer;

    private GameView mGameView;

    private Compass mCompass;
    private LocationRetriever mLocationRetriever;

    private Socket mSocket;

    private Emitter.Listener mOnRemotePlayerPositionsUpdated = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject data = (JSONObject) args[0];
            float latitude;
            float longitude;
            float rotation;

            try {
                latitude = (float) data.getDouble("x");
                longitude = (float) data.getDouble("y");
                rotation = (float) data.getDouble("rotation");

                Log.d("Position", String.valueOf(latitude) + "-" + String.valueOf(longitude) + "-" + String.valueOf(rotation));
            } catch (JSONException e) {
                return;
            }
        }
    };

    /**
     * @brief Constructor
     * @param context
     * @param gameView
     */
    public GameEngine(Context context, GameView gameView) {
        mContext = context;
        mGameView = gameView;

        mCompass = new Compass(mContext);
        mLocationRetriever = new LocationRetriever(mContext);

        mPlayers = new ArrayList<>();
    }

    /**
     * @brief Starts the game engine
     */
    public void start() {
        try {
            mSocket = IO.socket(SERVER_URI);
        } catch (URISyntaxException e)
        {

        }

        mSocket.on("m", mOnRemotePlayerPositionsUpdated);
        mSocket.connect();

        mSocket.emit("new_game");
        mSocket.emit("join_game", "0");

        initializeView();
    }

    /**
     * @brief Stops the game engine
     */
    public void stop() {
        stopSensors();
        mSocket.close();
        mSocket = null;
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
        JSONObject moveJsonObject = new JSONObject();

        moveJsonObject.put("x", mCurrentPlayer.getPosition().latitude);
        moveJsonObject.put("y", mCurrentPlayer.getPosition().longitude);
        moveJsonObject.put("rotation", mCurrentPlayer.getRotation());

        mSocket.emit("move", moveJsonObject);
    }
}
