package com.game.wargame.Activities;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.game.wargame.Communication.GameEngineSocket;
import com.game.wargame.Communication.PlayerSocket;
import com.game.wargame.Communication.RabbitMQ.RabbitMQSocket;
import com.game.wargame.Communication.RemoteCommunicationService;
import com.game.wargame.Communication.RemoteCommunicationServiceConnection;
import com.game.wargame.Entities.LocalPlayerModel;
import com.game.wargame.GameEngine.GameEngine;
import com.game.wargame.GameEngine.GameView;
import com.game.wargame.R;
import com.game.wargame.Sensors.LocationRetriever;
import com.game.wargame.WarGameApplication;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.UUID;

public class MapActivity extends FragmentActivity {

    private Context mContext;
    private WarGameApplication mApplication;

    private GameView mGameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mContext = this;

        mApplication = (WarGameApplication) mContext.getApplicationContext();
        mGameView = new GameView(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(mGameView);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = new Intent(this, RemoteCommunicationService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }


    @Override
    protected void onStop() {
        mApplication.getGameEngine().stop();

        if (mConnection.getBound()) {
            mConnection.getService().getGameEngineSocket().disconnect();
            unbindService(mConnection);
            mConnection.unbind();
        }
        super.onStop();
    }

    private RemoteCommunicationServiceConnection mConnection = new RemoteCommunicationServiceConnection(new RemoteCommunicationServiceConnection.OnServiceConnectedListener() {
        @Override
        public void onServiceConnectedListener() {

            // Get the current user
            Intent myIntent = getIntent();
            String username = myIntent.getStringExtra("username");
            String type = myIntent.getStringExtra("type");

            UUID gameRoomUUID = UUID.randomUUID();

            RabbitMQSocket rabbitMqSocket = new RabbitMQSocket("10.0.2.2");
            mConnection.getService().initialize(rabbitMqSocket);

            GameEngineSocket gameEngineSocket = mConnection.getService().getGameEngineSocket();
            gameEngineSocket.connect("game_room_" + gameRoomUUID.toString());

            GameEngine gameEngine = new GameEngine(mContext, gameEngineSocket, new LocationRetriever(mContext));
            mApplication.setGameEngine(gameEngine);

            PlayerSocket localPlayerSocket = mConnection.getService().getGameEngineSocket().getLocalPlayerSocket();
            LocalPlayerModel localPlayer = new LocalPlayerModel(username, localPlayerSocket);

            if(type == "create") {

            }
            else if(type == "join") {
            }

            gameEngineSocket.joinGame(localPlayer.getPlayerId(), localPlayer.getPlayerName());

            mApplication.getGameEngine().start(mGameView, localPlayer);
        }
    });
}
