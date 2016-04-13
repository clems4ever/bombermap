package com.game.wargame.Controller;

import android.graphics.Point;
import android.location.Location;
import android.util.Log;
import android.view.View;

import com.game.wargame.Controller.Communication.Game.GameSocket;
import com.game.wargame.Controller.Communication.Game.LocalPlayerSocket;
import com.game.wargame.Controller.Communication.Game.RemotePlayerSocket;
import com.game.wargame.Controller.Engine.DisplayCallback;
import com.game.wargame.Controller.Engine.GlobalTimer;
import com.game.wargame.Controller.GameLogic.CollisionManager;
import com.game.wargame.Controller.GameLogic.OnExplosionListener;
import com.game.wargame.Controller.Sensors.AbstractLocationRetriever;
import com.game.wargame.Controller.Sensors.OnLocationRetrievedListener;
import com.game.wargame.Controller.Settings.Settings;
import com.game.wargame.Model.Entities.EntitiesModel;
import com.game.wargame.Model.Entities.Entity;
import com.game.wargame.Model.Entities.Explosion;
import com.game.wargame.Model.Entities.Players.LocalPlayerModel;
import com.game.wargame.Model.Entities.Players.OnPlayerDiedListener;
import com.game.wargame.Model.Entities.Players.OnPlayerRespawnListener;
import com.game.wargame.Model.Entities.Players.OnPlayerWeaponTriggeredListener;
import com.game.wargame.Model.Entities.Players.OnRemotePlayerPositionUpdated;
import com.game.wargame.Model.Entities.Players.Player;
import com.game.wargame.Model.Entities.Players.PlayerModel;
import com.game.wargame.Model.Entities.Players.RemotePlayerModel;
import com.game.wargame.Model.Entities.Projectiles.Projectile;
import com.game.wargame.Model.Entities.VirtualMap.Cell;
import com.game.wargame.Model.Entities.VirtualMap.CellTypeEnum;
import com.game.wargame.Model.Entities.VirtualMap.RealCell;
import com.game.wargame.Model.Entities.VirtualMap.RealMap;
import com.game.wargame.Model.GameContext.FragManager;
import com.game.wargame.Model.GameContext.GameContext;
import com.game.wargame.Model.GameContext.GameNotificationManager;
import com.game.wargame.Views.Activities.GameMainFragment;
import com.game.wargame.Views.GameView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;


public class GameEngine implements OnPlayerWeaponTriggeredListener,
        OnPlayerDiedListener,
        GameSocket.OnPlayerEventListener,
        OnExplosionListener,
        OnPlayerRespawnListener,
        OnRemotePlayerPositionUpdated,
        OnLocationRetrievedListener {

    private static final int WEAPON_RANGE = 1000;

    private java.util.Map<String, PlayerModel> mPlayersById;
    private LocalPlayerModel mCurrentPlayer;
    private boolean mCurrentPlayerLocked;

    private GameView mGameView;
    private RealMap mVirtualMap;

    private AbstractLocationRetriever mLocationRetriever;

    private GlobalTimer mGlobalTimer;
    private GameSocket mGameSocket;

    private EntitiesModel mEntitiesModel;
    private GameContext mGameContext;

    private GameMainFragment.Callback mGameCallback;

    // Path Editor for debug
    private LinkedList<LatLng> mPathEditor;

    /**
     * @brief Constructor
     */
    public GameEngine() {
        mPlayersById = new HashMap<>();
        mEntitiesModel = new EntitiesModel();
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
        mGameView.updateVirtualMapOverlay(mVirtualMap);

        startLocationRetriever();
        initializeView();
        startGlobalUpdateTimer();
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
        mGlobalTimer.setEntitiesModel(mEntitiesModel);
        mGlobalTimer.setCurrentPlayerModel(mCurrentPlayer);
        mGlobalTimer.setCollisionManager(new CollisionManager(new com.game.wargame.Controller.Utils.Location()));
        DisplayCallback displayCallback = new DisplayCallback(mGameView, mGameContext, mCurrentPlayer, mEntitiesModel);
        mGlobalTimer.setDisplayCallback(displayCallback);
        mGlobalTimer.setGameContext(mGameContext);
        mGlobalTimer.setGameCallback(mGameCallback);
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
                mGameView.moveCameraTo(mCurrentPlayer.getPosition(), 17);
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
    private void addPlayer(PlayerModel player) {
        player.setOnPlayerWeaponTriggeredListener(this);
        player.setOnPlayerDiedListener(this);
        mGameContext.addPlayer(player.getPlayerId());
        mPlayersById.put(player.getPlayerId(), player);
    }

    public void addLocalPlayer(LocalPlayerModel p) {
        addPlayer(p);
    }

    public void addRemotePlayer(RemotePlayerModel p) {
        p.setOnRemotePlayerPositionUpdated(this);
        addPlayer(p);
    }

    @Override
    public void onRemotePlayerPositionChanged(PlayerModel player) {
        mGameView.movePlayer(player);
    }

    private boolean isPositionOnVirtualMap(double latitude, double longitude) {
        LatLng position = new LatLng(latitude, longitude);
        boolean collision = false;

        for(int x=0; x < mVirtualMap.width() && !collision; ++x) {
            for(int y=0; y<mVirtualMap.height() && !collision; ++y) {
                RealCell realCell = mVirtualMap.getRealCell(x, y);
                if(realCell.type() == CellTypeEnum.BLOCK) {
                    collision |= PolyUtil.containsLocation(position, realCell.vertices(), false);
                }
            }
        }
        return collision;
    }

    @Override
    public void onLocationRetrieved(double latitude, double longitude) {

        // If the new position is on the map, then lock the user and move its shadow only
        if(isPositionOnVirtualMap(latitude, longitude)) {
            mCurrentPlayer.moveShadow(latitude, longitude);
            //mCurrentPlayerLocked = true;
            Log.d("Collision", "Collision has been detected.");
        }

        if(!mCurrentPlayerLocked) {
            mCurrentPlayer.move(latitude, longitude);
            mGameView.movePlayer(mCurrentPlayer);
        }
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
    public void onExplosion(Entity entity, long time) {
        entity.setToRemove(true);
        mEntitiesModel.addEntity(new Explosion(entity.getOwner(), (double) time, entity.getPosition(), entity.getDirection()));
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
}
