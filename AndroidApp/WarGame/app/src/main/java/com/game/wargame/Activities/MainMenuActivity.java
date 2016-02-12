package com.game.wargame.Activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.game.wargame.Communication.RemoteCommunicationService;
import com.game.wargame.Communication.RemoteCommunicationServiceConnection;
import com.game.wargame.Communication.RemoteCommunicationSystem;
import com.game.wargame.R;

public class MainMenuActivity extends AppCompatActivity {

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        mContext = this;
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = new Intent(this, RemoteCommunicationService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }


    @Override
    protected void onStop() {
        if (mConnection.getBound()) {
            unbindService(mConnection);
            mConnection.unbind();
        }

        super.onStop();
    }

    private RemoteCommunicationServiceConnection mConnection = new RemoteCommunicationServiceConnection(new RemoteCommunicationServiceConnection.OnServiceConnectedListener() {
        @Override
        public void onServiceConnectedListener() {

            Button createGameButton = (Button) findViewById(R.id.new_game_button);
            Button joinGameButton = (Button) findViewById(R.id.join_button);

            final EditText userNameEditText = (EditText) findViewById(R.id.username_edittext);

            createGameButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    mConnection.getService().getRemoteCommunicationSystem().createGame(new RemoteCommunicationSystem.OnGameCreatedListener() {
                        @Override
                        public void onGameCreated(String gameId) {
                            Log.d("MainMenuActivity", "Game \"" + gameId + "\" created");

                            final String username = userNameEditText.getText().toString();

                            mConnection.getService().getRemoteCommunicationSystem().joinGame(gameId, username, new RemoteCommunicationSystem.OnGameJoinedListener() {
                                @Override
                                public void onGameJoined() {
                                    Intent intent = new Intent(mContext, RoomActivity.class);
                                    startActivity(intent);
                                }
                            });
                        }
                    });
                }
            });

            joinGameButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final String username = userNameEditText.getText().toString();

                    mConnection.getService().getRemoteCommunicationSystem().joinGame("0", username, new RemoteCommunicationSystem.OnGameJoinedListener() {
                        @Override
                        public void onGameJoined() {
                            Intent intent = new Intent(mContext, RoomActivity.class);
                            startActivity(intent);
                        }
                    });
                }
            });

        }
    });
}
