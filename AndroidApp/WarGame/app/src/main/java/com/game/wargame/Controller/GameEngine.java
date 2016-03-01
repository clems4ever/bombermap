package com.game.wargame.Controller;

import android.graphics.Point;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.game.wargame.Controller.Communication.Game.GameSocket;
import com.game.wargame.Controller.Communication.Game.LocalPlayerSocket;
import com.game.wargame.Controller.Communication.Game.RemotePlayerSocket;
import com.game.wargame.Model.Entities.LocalPlayerModel;
import com.game.wargame.Model.Entities.OnPlayerPositionChangedListener;
import com.game.wargame.Model.Entities.OnPlayerWeaponTriggeredListener;
import com.game.wargame.Model.Entities.Player;
import com.game.wargame.Model.Entities.PlayerModel;
import com.game.wargame.Model.Entities.Projectile;
import com.game.wargame.Model.Entities.RemotePlayerModel;
import com.game.wargame.Model.Entities.ProjectileModel;
import com.game.wargame.Views.GameView;
import com.game.wargame.Controller.Sensors.LocationRetriever;
import com.game.wargame.Views.MapView;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.LogRecord;


public class GameEngine implements OnPlayerPositionChangedListener, OnPlayerWeaponTriggeredListener, GameSocket.OnPlayerEventListener {

    private static final int WEAPON_TIME = 100;

    private Map<String, PlayerModel> mPlayersById;
    private LocalPlayerModel mCurrentPlayer;

    private GameView mGameView;

    private LocationRetriever mLocationRetriever;

    private GameSocket mGameSocket;

    /**
     * @brief Constructor
     */
    public GameEngine() {
        mPlayersById = new HashMap<>();
    }

    /**
     * @brief Starts the game engine
     */
    public void onStart(GameView gameView, GameSocket gameSocket, LocalPlayerSocket localPlayerSocket, LocationRetriever locationRetriever) {
        mGameView = gameView;
        mGameSocket = gameSocket;
        mLocationRetriever = locationRetriever;

        mGameSocket.setOnPlayerEventListener(this);

        mCurrentPlayer = new LocalPlayerModel("username", localPlayerSocket);
        addPlayer(mCurrentPlayer);

        startSensors();
        initializeView();
        //startUpdatingProjectiles();
    }

    /**
     * @brief Stops the game engine
     */
    public void onStop() {
        mCurrentPlayer.leave();
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

                if (distanceInMeters < 1000) {
                    onPlayerWeaponTriggeredListener(mCurrentPlayer, targetPosition.latitude, targetPosition.longitude, WEAPON_TIME);
                    mCurrentPlayer.fire(targetPosition.latitude, targetPosition.longitude, WEAPON_TIME);
                } else {
                    Log.d("GameEngine", "The target is out of range");
                    mGameView.onActionFinished();
                }
            }
        });

        mGameView.setOnGpsButtonClickedListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGameView.moveCameraTo(mCurrentPlayer.getPosition(), 4);
            }
        });

        mGameView.loadMap(new MapView.OnMapReadyListener() {
            @Override
            public void onMapReady() {
                Set<Map.Entry<String, PlayerModel>> entrySet = mPlayersById.entrySet();
                Iterator<Map.Entry<String, PlayerModel>> iterator = entrySet.iterator();

                while (iterator.hasNext()) {
                    Map.Entry<String, PlayerModel> entry = iterator.next();

                    mGameView.movePlayer(entry.getValue(), entry.getValue() == mCurrentPlayer);
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
        mPlayersById.put(player.getPlayerId(), player);
    }

    /**
     * @brief Updating all non playing elements of the game in a separate thread
     */
    private void startUpdatingProjectiles()
    {
        final Handler handler = new Handler(Looper.getMainLooper()) {
            /*
             * handleMessage() defines the operations to perform when
             * the Handler receives a new Message to process.
             */
            @Override
            public void handleMessage(Message inputMessage) {
                mGameView.displayProjectiles();
            }
        };

        Runnable r = new Runnable() {
            public void run() {
                //TODO: get Timer value here
                double time = 0;
                //TODO: Wait for a tick to happen
                /*Set<Projectile> projectiles = ProjectileModel.getProjectiles();
                for (Projectile projectile : projectiles) {
                    update(projectile, time);
                }*/
                //send empty message to notify UI thread to display the projectiles;
                handler.sendEmptyMessage(0);
            }
        };
        Thread thread = new Thread(r);
        thread.start();
    }

    private void update(Projectile projectile, double time) {
        //TODO: interpolate from timeToDestroy, Tick, position and target to reach destination on time

    }

    @Override
    public void onPlayerPositionChanged(PlayerModel player) {
        mGameView.movePlayer(player, player == mCurrentPlayer);
    }

    @Override
    public void onPlayerWeaponTriggeredListener(PlayerModel player, double latitude, double longitude, double speed) {
        LatLng source = player.getPosition();
        LatLng destination = new LatLng(latitude, longitude);

        mGameView.triggerWeapon(source, destination, speed);
    }

    public LocalPlayerModel getLocalPlayer() {
        return mCurrentPlayer;
    }

    public int getPlayersCount() {
        return mPlayersById.size();
    }

    // A player has sent a join event, we must send him back a join event
    @Override
    public void onPlayerJoined(RemotePlayerSocket playerSocket) {
        RemotePlayerModel player = new RemotePlayerModel("username", playerSocket);
        addPlayer(player);
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

    public class UpdateProjectilesTask extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... params) {

            //Manage collisions and position updations
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {

        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            //update UI?
        }
    }

}
