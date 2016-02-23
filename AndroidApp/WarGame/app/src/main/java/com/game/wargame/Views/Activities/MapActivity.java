package com.game.wargame.Views.Activities;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.game.wargame.Controller.Communication.IEventSocket;
import com.game.wargame.Controller.GameEngine;
import com.game.wargame.Controller.Communication.GameEngineSocket;
import com.game.wargame.Controller.Communication.PlayerSocket;
import com.game.wargame.Controller.Communication.RabbitMQ.RabbitMQSocket;
import com.game.wargame.Controller.Communication.RemoteCommunicationService;
import com.game.wargame.Controller.Communication.RemoteCommunicationServiceConnection;
import com.game.wargame.Controller.Sensors.LocationRetriever;
import com.game.wargame.Model.Entities.LocalPlayerModel;
import com.game.wargame.R;
import com.game.wargame.Views.GameView;
import com.game.wargame.WarGameApplication;

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
            final String username = myIntent.getStringExtra("username");
            String type = myIntent.getStringExtra("type");

            String gameRoomId = "abc";

            RabbitMQSocket rabbitMqSocket = new RabbitMQSocket("10.0.2.2", gameRoomId);
            mConnection.getService().initialize(rabbitMqSocket);

            GameEngineSocket gameEngineSocket = mConnection.getService().getGameEngineSocket();
            gameEngineSocket.setOnDisconnectedListener(new IEventSocket.OnDisconnectedListener() {
                @Override
                public void onDisconnected() {
                    finish();
                }
            });
            gameEngineSocket.connect();

            GameEngine gameEngine = new GameEngine(mContext, gameEngineSocket, new LocationRetriever(mContext));
            mApplication.setGameEngine(gameEngine);

            if(type.equals("create")) {

            }

            gameEngineSocket.joinGame(username, new GameEngineSocket.OnJoinedListener() {
                @Override
                public void onJoined(String playerId) {
                    PlayerSocket localPlayerSocket = mConnection.getService().getGameEngineSocket().getLocalPlayerSocket();
                    LocalPlayerModel localPlayer = new LocalPlayerModel(username, localPlayerSocket);

                    mApplication.getGameEngine().start(mGameView, localPlayer);
                }
            });
        }
    });
}
