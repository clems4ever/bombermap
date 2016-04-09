package com.game.wargame.Controller;

import android.graphics.Point;
import android.location.Location;
import android.util.Log;
import android.view.View;

import com.game.wargame.Controller.Communication.Game.GameSocket;
import com.game.wargame.Controller.Communication.Game.LocalPlayerSocket;
import com.game.wargame.Controller.Communication.Game.RemotePlayerSocket;
import com.game.wargame.Controller.Engine.EntitiesUpdateCallback;
import com.game.wargame.Controller.Engine.GlobalTimer;
import com.game.wargame.Controller.GameLogic.CollisionManager;
import com.game.wargame.Controller.GameLogic.OnExplosionListener;
import com.game.wargame.Controller.Sensors.AbstractLocationRetriever;
import com.game.wargame.Controller.Sensors.LocationRetriever;
import com.game.wargame.Controller.Sensors.OnLocationRetrievedListener;
import com.game.wargame.Model.Entities.EntitiesModel;
import com.game.wargame.Model.Entities.Entity;
import com.game.wargame.Model.Entities.Explosion;
import com.game.wargame.Model.Entities.Players.LocalPlayerModel;
import com.game.wargame.Model.Entities.Players.OnPlayerPositionChangedListener;
import com.game.wargame.Model.Entities.Players.OnPlayerWeaponTriggeredListener;
import com.game.wargame.Model.Entities.Players.PlayerModel;
import com.game.wargame.Model.Entities.Players.RemotePlayerModel;
import com.game.wargame.Model.Entities.Projectiles.Projectile;
import com.game.wargame.Views.GameView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;


public class GameEngine implements OnPlayerPositionChangedListener, OnPlayerWeaponTriggeredListener, GameSocket.OnPlayerEventListener, OnExplosionListener, OnLocationRetrievedListener {

    private static final int WEAPON_TIME = 100;
    private static final int WEAPON_RANGE = 1000;

    private Map<String, PlayerModel> mPlayersById;
    private LocalPlayerModel mCurrentPlayer;

    private GameView mGameView;

    private AbstractLocationRetriever mLocationRetriever;

    private GlobalTimer mGlobalTimer;
    private GameSocket mGameSocket;

    private EntitiesModel mEntitiesModel;
    private CollisionManager mCollisionManager;

    // Path Editor for debug
    private LinkedList<LatLng> mPathEditor;

    /**
     * @brief Constructor
     */
    public GameEngine() {
        mPlayersById = new HashMap<>();
        mEntitiesModel = new EntitiesModel();
        mPathEditor = new LinkedList<>();
    }

    /**
     * @brief Starts the game engine
     */
    public void onStart(GameView gameView, GameSocket gameSocket, LocalPlayerSocket localPlayerSocket, AbstractLocationRetriever locationRetriever, GlobalTimer globalTimer) {
        mGameView = gameView;
        mGameSocket = gameSocket;
        mGlobalTimer = globalTimer;
        mLocationRetriever = locationRetriever;

        mGameSocket.setOnPlayerEventListener(this);
        mGameSocket.setOnClockEventListener(mGlobalTimer);

        mCurrentPlayer = new LocalPlayerModel("username", localPlayerSocket);
        addPlayer(mCurrentPlayer);
        mGameView.addLocalPlayer(mCurrentPlayer);

        startLocationRetriever();
        initializeView();
        startEntitiesUpdateTimer();
    }

    /**
     * @brief Stops the game engine
     */
    public void onStop() {
        mCurrentPlayer.leave();
        stopLocationRetriever();
        stopGameTimers();
    }

    /**
     * @brief Starts the sensors and listen to events
     */
    private void startLocationRetriever() {
        mLocationRetriever.setOnLocationRetrievedListener(this);
        mLocationRetriever.start();
    }

    /**
     * @brief Stops listening to the sensors
     */
    private void stopLocationRetriever() {
        mLocationRetriever.stop();
    }

    private void startEntitiesUpdateTimer() {
        mGlobalTimer.setEntitiesModel(mEntitiesModel);
        mGlobalTimer.setPlayersModel(mCurrentPlayer);
        mGlobalTimer.setCollisionManager(new CollisionManager());
        mGlobalTimer.setGameView(mGameView);
        mGlobalTimer.setUpdateCallback(new EntitiesUpdateCallback());
        mGlobalTimer.start();
    }

    private void stopGameTimers() {
        mGlobalTimer.stop();
    }

    private double getTime() {
        return (double) mGlobalTimer.getTicks()*mGlobalTimer.UPDATE_SAMPLE_TIME;
    }

    /**
     * @brief Initialize the view
     */
    private void initializeView() {
        mGameView.initialize(new GameView.OnWeaponTargetDefinedListener() {
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

                if (distanceInMeters < WEAPON_RANGE) {
                    mCurrentPlayer.fire(targetPosition.latitude, targetPosition.longitude, getTime());
                } else {
                    Log.d("GameEngine", "The target is out of range");
                    mGameView.onActionFinished();
                }
            }
        });

        mGameView.setOnGpsButtonClickedListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGameView.moveCameraTo(mCurrentPlayer.getPosition(), 15);
            }
        });

        mGameView.getMapView().setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng position) {
                mPathEditor.push(position);
            }
        });

        mGameView.getMapView().setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng _) {
                Iterator<LatLng> it = mPathEditor.iterator();
                JSONArray jsonArray = new JSONArray();

                while (it.hasNext()) {
                    LatLng position = it.next();

                    JSONObject latLngJson = new JSONObject();
                    try {
                        latLngJson.put("lat", position.latitude);
                        latLngJson.put("long", position.longitude);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    jsonArray.put(latLngJson);
                }
                Log.d("MapView touch debug", jsonArray.toString());
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
        mPlayersById.put(player.getPlayerId(), player);
    }

    @Override
    public void onPlayerPositionChanged(PlayerModel player) {
        mGameView.movePlayer(player);
    }

    private boolean isPositionOnVirtualMap(double latitude, double longitude) {
        return false;
    }

    @Override
    public void onPlayerWeaponTriggeredListener(PlayerModel player, double latitude, double longitude, double timestamp) {
        LatLng source = player.getPosition();
        LatLng destination = new LatLng(latitude, longitude);

        Projectile projectile = new Projectile(player.getPlayerId(), source, destination, timestamp);
        projectile.setOnExplosionListener(this);
        mEntitiesModel.addEntity(projectile);
    }

    public int getPlayersCount() {
        return mPlayersById.size();
    }

    // A player has sent a join event, we must send him back a join event
    @Override
    public void onPlayerJoined(RemotePlayerSocket playerSocket) {
        RemotePlayerModel player = new RemotePlayerModel("username", playerSocket);
        addPlayer(player);
        mGameView.addRemotePlayer(player);
    }

    // A remote player has left the game
    @Override
    public void onPlayerLeft(RemotePlayerSocket playerSocket) {
        PlayerModel playerModel = mPlayersById.get(playerSocket.getPlayerId());
        if(playerModel != null) {
            mGameView.removePlayer(playerModel);
            mPlayersById.remove(playerSocket.getPlayerId());
        }
    }

    @Override
    public void onExplosion(Entity entity, long time) {
        entity.setToRemove(true);
        mEntitiesModel.addEntity(new Explosion(entity.getOwner(), (double)time, entity.getPosition(), entity.getDirection()));
    }

    @Override
    public void onLocationRetrieved(double latitude, double longitude) {

    }
}
