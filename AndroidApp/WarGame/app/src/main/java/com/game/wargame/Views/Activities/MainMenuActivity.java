package com.game.wargame.Views.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.game.wargame.Controller.Communication.ConnectionManager;
import com.game.wargame.Controller.Communication.Game.GameManagerSocket;
import com.game.wargame.Controller.Communication.Game.GameSocket;
import com.game.wargame.Controller.Communication.IConnectionManager;
import com.game.wargame.Controller.Communication.Game.LocalPlayerSocket;
import com.game.wargame.R;
import com.game.wargame.WarGameApplication;

public class MainMenuActivity extends Activity {

    private Context mContext;
    private IConnectionManager mConnectionManager;

    private WarGameApplication mApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        mContext = this;
        mApplication = (WarGameApplication) getApplication();

        Button createGameButton = (Button) findViewById(R.id.new_game_button);
        Button joinGameButton = (Button) findViewById(R.id.join_button);

        final EditText userNameEditText = (EditText) findViewById(R.id.username_edittext);

        createGameButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final String username = userNameEditText.getText().toString();

                if (!username.isEmpty()) {
                    createGame(username);
                } else {
                    displayAlertDialog();
                }
            }
        });

        joinGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = userNameEditText.getText().toString();
                String gameId = "abc";

                if (!username.isEmpty()) {
                    joinGame(gameId, username);
                } else {
                    displayAlertDialog();
                }
            }
        });
    }

    private void createGame(final String username) {
        GameManagerSocket gameManagerSocket = mConnectionManager.buildGameManagerSocket();

        gameManagerSocket.createGame(new GameManagerSocket.OnGameCreatedListener() {
            @Override
            public void onGameCreated(String gameId) {
                joinGame(gameId, username);
            }
        });
    }

    private void joinGame(final String gameId, final String username) {
        GameManagerSocket gameManagerSocket = mConnectionManager.buildGameManagerSocket();
        gameManagerSocket.joinGame(gameId, new GameManagerSocket.OnGameJoinedListener() {
            @Override
            public void onGameJoined(GameSocket gameSocket, LocalPlayerSocket localPlayerSocket) {
                mApplication.mGameSocket = gameSocket;
                mApplication.mLocalPlayerSocket = localPlayerSocket;

                Intent intent = new Intent(mContext, GameActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });
    }

    private void displayAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        alertDialog.setTitle("Wrong username");
        alertDialog.setMessage("Please type a valid user name.");

        alertDialog.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mConnectionManager = ConnectionManager.onStart();
    }


    @Override
    protected void onStop() {
        ConnectionManager.onStop();
        super.onStop();
    }
}
