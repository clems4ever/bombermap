package com.game.wargame.Controller;

import android.graphics.Point;
import android.location.Location;
import android.util.Log;
import android.view.View;

import com.game.wargame.Controller.Communication.Game.GameSocket;
import com.game.wargame.Controller.Communication.Game.LocalPlayerSocket;
import com.game.wargame.Controller.Communication.Game.RemotePlayerSocket;
import com.game.wargame.Controller.Engine.DisplayCallback;
import com.game.wargame.Controller.Engine.DisplayCommands.AddProjectileDisplayCommand;
import com.game.wargame.Controller.Engine.GlobalTimer;
import com.game.wargame.Controller.GameLogic.CollisionManager;
import com.game.wargame.Controller.Sensors.AbstractLocationRetriever;
import com.game.wargame.Controller.Sensors.OnLocationRetrievedListener;
import com.game.wargame.Controller.Settings.Settings;
import com.game.wargame.Model.Entities.EntitiesContainer;
import com.game.wargame.Model.Entities.Players.LocalPlayerModel;
import com.game.wargame.Model.Entities.Players.OnPlayerDiedListener;
import com.game.wargame.Model.Entities.Players.OnPlayerRespawnListener;
import com.game.wargame.Model.Entities.Players.OnPlayerShielded;
import com.game.wargame.Model.Entities.Players.OnPlayerWeaponTriggeredListener;
import com.game.wargame.Model.Entities.Players.OnRemotePlayerPositionUpdated;
import com.game.wargame.Model.Entities.Players.Player;
import com.game.wargame.Model.Entities.Players.PlayerModel;
import com.game.wargame.Model.Entities.Players.RemotePlayerModel;
import com.game.wargame.Model.Entities.Projectiles.Projectile;
import com.game.wargame.Model.Entities.VirtualMap.CellTypeEnum;
import com.game.wargame.Model.Entities.VirtualMap.RealCell;
import com.game.wargame.Model.Entities.VirtualMap.RealMap;
import com.game.wargame.Model.GameContext.FragManager;
import com.game.wargame.Model.GameContext.GameContext;
import com.game.wargame.Model.GameContext.GameNotificationManager;
import com.game.wargame.Views.Activities.GameMainFragment;
import com.game.wargame.Views.Views.GameView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;


public class GameEngine implements OnPlayerWeaponTriggeredListener,
        OnPlayerDiedListener,
        GameSocket.OnPlayerEventListener,
        OnPlayerRespawnListener,
        OnRemotePlayerPositionUpdated,
        OnLocationRetrievedListener, OnPlayerShielded {

    private static final int WEAPON_RANGE = 1000;

    private java.util.Map<String, PlayerModel> mPlayersById;
    private LocalPlayerModel mCurrentPlayer;
    private boolean mCurrentPlayerLocked;

    private GameView mGameView;
    private RealMap mVirtualMap;

    private AbstractLocationRetriever mLocationRetriever;

    private GlobalTimer mGlobalTimer;
    private GameSocket mGameSocket;

    private EntitiesContainer mEntitiesContainer;
    private DisplayCallback mDisplayCallback;
    private GameContext mGameContext;

    private GameMainFragment.Callback mGameCallback;

    // Path Editor for debug
    private LinkedList<LatLng> mPathEditor;

    /**
     * @brief Constructor
     */
    public GameEngine() {
        mPlayersById = new HashMap<>();
        mPathEditor = new LinkedList<>();
        mCurrentPlayerLocked = false;
    }

    /**
     * @brief Starts the game engine
     */
    public void onStart(GameView gameView,
                        GameSocket gameSocket,
                        RealMap virtualMap,
                        LocalPlayerSocket localPlayerSocket,
                        AbstractLocationRetriever locationRetriever,
                        GlobalTimer globalTimer,
                        Settings settings) {

        mEntitiesContainer = new EntitiesContainer();
        mGameView = gameView;
        mGameSocket = gameSocket;
        mGlobalTimer = globalTimer;
        mLocationRetriever = locationRetriever;
        mVirtualMap = virtualMap;

        mGameSocket.setOnPlayerEventListener(this);
        mGameSocket.setOnClockEventListener(mGlobalTimer);

        FragManager fragManager = new FragManager();
        GameNotificationManager gameNotificationManager = new GameNotificationManager();
        mGameContext = new GameContext(fragManager, gameNotificationManager, settings.gameDuration);

        mCurrentPlayer = new LocalPlayerModel("username", localPlayerSocket);
        addLocalPlayer(mCurrentPlayer);
        mGameView.addLocalPlayer(mCurrentPlayer);

        addBlocksAsEntities(mVirtualMap);

        startLocationRetriever();
        initializeView();
        startGlobalUpdateTimer();
    }

    private void addBlocksAsEntities(RealMap map) {
        for(int i=0; i < map.width(); ++i) {
            for(int j=0; j < map.height(); ++j) {
                if(map.getRealCell(i, j).cell().type() == CellTypeEnum.BLOCK) {
                    mEntitiesContainer.addBlock(map.getRealCell(i, j));
                }
            }
        }
    }

    /**
     * @brief Stops the game engine
     */
    public void onStop() {
        mCurrentPlayer.leave();
        mGameView.removePlayer(mCurrentPlayer);

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

    private void startGlobalUpdateTimer() {
        mGlobalTimer.setEntitiesModel(mEntitiesContainer);
        mGlobalTimer.setCurrentPlayerModel(mCurrentPlayer);
        mGlobalTimer.setCollisionManager(new CollisionManager(new com.game.wargame.Controller.Utils.Location()));
        DisplayCallback displayCallback = new DisplayCallback(mGameView, mGameContext, mCurrentPlayer, mEntitiesContainer);
        mDisplayCallback = displayCallback;
        mGlobalTimer.setDisplayCallback(mDisplayCallback);
        mGlobalTimer.setGameContext(mGameContext);
        mGlobalTimer.setGameCallback(mGameCallback);
        mGlobalTimer.setView(mGameView);
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
        }, new GameView.OnShieldListener() {

            @Override
            public void onShield() {
                mCurrentPlayer.shield(mGlobalTimer.getTicks()*mGlobalTimer.UPDATE_SAMPLE_TIME);
            }
        });

        mGameView.setOnGpsButtonClickedListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGameView.moveCameraTo(mCurrentPlayer.getPosition(), 17);
            }
        });

        mGameView.getMapView().setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng position) {
                mPathEditor.push(position);
            }
        });

        /*mGameView.getMapView().setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
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
                mPathEditor.clear();
            }
        });*/
    }

    /**
     * @brief Adding a player to the game
     * @param player
     */
    private void addPlayer(PlayerModel player) {
        player.setOnPlayerWeaponTriggeredListener(this);
        player.setOnPlayerDiedListener(this);
        player.setOnPlayerShieldListener(this);
        mGameContext.addPlayer(player.getPlayerId());
        mPlayersById.put(player.getPlayerId(), player);
    }

    public void addLocalPlayer(LocalPlayerModel p) {
        addPlayer(p);
    }

    public void addRemotePlayer(RemotePlayerModel p) {
        p.setOnRemotePlayerPositionUpdated(this);
        addPlayer(p);
        mGlobalTimer.addRemotePlayer(p);
        mDisplayCallback.addRemotePlayer(p);
    }

    @Override
    public void onRemotePlayerPositionChanged(PlayerModel player) {
        mGameView.movePlayer(player);
    }

    private boolean isPositionOnVirtualMap(double latitude, double longitude) {
        LatLng position = new LatLng(latitude, longitude);
        boolean collision = false;

        ArrayList<RealCell> blocks = mEntitiesContainer.getRealCells();
        Iterator<RealCell> it = blocks.iterator();

        while(it.hasNext()) {
            RealCell realCell = it.next();
            if(realCell.cell().type() == CellTypeEnum.BLOCK) {
                collision |= PolyUtil.containsLocation(position, realCell.vertices(), false);
            }
        }
        return collision;
    }

    @Override
    public void onLocationRetrieved(double latitude, double longitude) {

        boolean lockedLastState = mCurrentPlayerLocked;
        mCurrentPlayer.moveShadow(latitude, longitude);

        // If the new position is on the map, then lock the user and move its shadow only
        if(isPositionOnVirtualMap(latitude, longitude)) {
            mCurrentPlayerLocked = true;
            Log.d("Collision", "Collision has been detected.");
        }
        else {
            float[] results = new float[1];
            Location.distanceBetween(latitude, longitude, mCurrentPlayer.getPosition().latitude, mCurrentPlayer.getPosition().longitude, results);

            float distanceInMeters = results[0];
            if(distanceInMeters <= 10) {
                mCurrentPlayerLocked = false;
            }
        }

        // If lock state has changed
        if(lockedLastState != mCurrentPlayerLocked) {
            if(mCurrentPlayerLocked) {
                mGameView.getMapView().addPlayerShadow(mCurrentPlayer.getShadowPosition());
            }
            else {
                mGameView.getMapView().removePlayerShadow();
            }
        }

        // Refresh view
        if(!mCurrentPlayerLocked) {
            mCurrentPlayer.move(latitude, longitude, mGlobalTimer.getTime());
            mGameView.movePlayer(mCurrentPlayer);
        }
        else {
            mGameView.getMapView().movePlayerShadow(mCurrentPlayer.getShadowPosition());
        }
    }

    @Override
    public void onPlayerWeaponTriggeredListener(PlayerModel player, double latitude, double longitude, double timestamp) {
        LatLng source = player.getPosition();
        LatLng destination = new LatLng(latitude, longitude);

        Projectile projectile = new Projectile(player.getPlayerId(), source, destination, timestamp);
        mEntitiesContainer.addProjectile(projectile);
        mGlobalTimer.scheduleDisplayCommand(new AddProjectileDisplayCommand(projectile));
    }

    public int getPlayersCount() {
        return mPlayersById.size();
    }

    // A player has sent a join event, we must send him back a join event
    @Override
    public void onPlayerJoined(RemotePlayerSocket playerSocket) {
        RemotePlayerModel player = new RemotePlayerModel("username", playerSocket);
        addRemotePlayer(player);
        mGameView.addRemotePlayer(player);
        mCurrentPlayer.syncGameStart(mGameContext.getTimeStart());
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
    public void onDied(String dead, String killer, double time) {
        Player playerKiller = mPlayersById.get(killer);
        Player playerDead = mPlayersById.get(dead);
        if (playerDead != null && playerKiller != null)
            mGameContext.handleFrag(playerDead, playerKiller, time);
    }

    @Override
    public void onRespawn(String playerId, double time) {

    }

    public void setCallback(GameMainFragment.Callback callback) {
        this.mGameCallback = callback;
    }

    @Override
    public void onShield(String playerId, double time) {
        Player player = mPlayersById.get(playerId);
        if (player != null)
            player.shield();
    }
}
