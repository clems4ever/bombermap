package com.game.wargame.Controller;

import android.graphics.Point;
import android.location.Location;
import android.util.Log;
import android.view.View;

import com.game.wargame.Controller.Communication.Game.GameSocket;
import com.game.wargame.Controller.Communication.Game.LocalPlayerSocket;
import com.game.wargame.Controller.Communication.Game.RemotePlayerSocket;
import com.game.wargame.Controller.Engine.UpdateCallback;
import com.game.wargame.Controller.Engine.GlobalTimer;
import com.game.wargame.Controller.GameLogic.CollisionManager;
import com.game.wargame.Model.Entities.OnPlayerDiedListener;
import com.game.wargame.Model.Entities.Player;
import com.game.wargame.Model.FragManager;
import com.game.wargame.Model.GameContext;
import com.game.wargame.Controller.GameLogic.OnExplosionListener;
import com.game.wargame.Model.Entities.Entity;
import com.game.wargame.Model.Entities.Explosion;
import com.game.wargame.Model.Entities.LocalPlayerModel;
import com.game.wargame.Model.Entities.OnPlayerPositionChangedListener;
import com.game.wargame.Model.Entities.OnPlayerWeaponTriggeredListener;
import com.game.wargame.Model.Entities.PlayerModel;
import com.game.wargame.Model.Entities.Projectile;
import com.game.wargame.Model.Entities.RemotePlayerModel;
import com.game.wargame.Model.Entities.EntitiesModel;
import com.game.wargame.Views.GameView;
import com.game.wargame.Controller.Sensors.LocationRetriever;
import com.game.wargame.Views.MapView;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class GameEngine implements OnPlayerPositionChangedListener, OnPlayerWeaponTriggeredListener, OnPlayerDiedListener, GameSocket.OnPlayerEventListener, OnExplosionListener {

    private static final int WEAPON_TIME = 100;
    private static final int WEAPON_RANGE = 1000;

    private Map<String, PlayerModel> mPlayersById;
    private LocalPlayerModel mCurrentPlayer;

    private GameView mGameView;

    private LocationRetriever mLocationRetriever;

    private GlobalTimer mGlobalTimer;
    private GameSocket mGameSocket;

    private EntitiesModel mEntitiesModel;
    private GameContext mGameContext;

    /**
     * @brief Constructor
     */
    public GameEngine() {
        mPlayersById = new HashMap<>();
        mEntitiesModel = new EntitiesModel();
    }

    /**
     * @brief Starts the game engine
     */
    public void onStart(GameView gameView, GameSocket gameSocket, LocalPlayerSocket localPlayerSocket, LocationRetriever locationRetriever, GlobalTimer globalTimer) {
        mGameView = gameView;
        mGameSocket = gameSocket;
        mGlobalTimer = globalTimer;
        mLocationRetriever = locationRetriever;

        mGameSocket.setOnPlayerEventListener(this);
        mGameSocket.setOnClockEventListener(mGlobalTimer);

        mCurrentPlayer = new LocalPlayerModel("username", localPlayerSocket);
        addPlayer(mCurrentPlayer);
        mGameView.addLocalPlayer(mCurrentPlayer);

        Set<String> playerIds = mPlayersById.keySet();
        FragManager fragManager = new FragManager(playerIds);
        mGameContext = new GameContext(fragManager);

        startSensors();
        initializeView();
        startGlobalUpdateTimer();
    }

    /**
     * @brief Stops the game engine
     */
    public void onStop() {
        mCurrentPlayer.leave();
        stopSensors();
        stopGameTimers();
    }

    /**
     * @brief Starts the sensors and listen to events
     */
    private void startSensors() {
        mLocationRetriever.start(mCurrentPlayer);
    }

    private void startGlobalUpdateTimer() {
        mGlobalTimer.setEntitiesModel(mEntitiesModel);
        mGlobalTimer.setCurrentPlayerModel(mCurrentPlayer);
        mGlobalTimer.setCollisionManager(new CollisionManager());
        mGlobalTimer.setGameView(mGameView);
        mGlobalTimer.setGameContext(mGameContext);
        mGlobalTimer.setUpdateCallback(new UpdateCallback());
        mGlobalTimer.start();
    }

    private void stopGameTimers() {
        mGlobalTimer.stop();
    }

    private double getTime() {
        return (double) mGlobalTimer.getTicks()*mGlobalTimer.UPDATE_SAMPLE_TIME;
    }

    /**
     * @brief Stops listening to the sensors
     */
    private void stopSensors() {
        mLocationRetriever.stop();
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
    public void onDied(String dead, String killer) {
        mGameContext.addFrag(killer);
        mGameContext.addDeath(dead);
    }
}
