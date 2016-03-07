package com.game.wargame.Views.Activities;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.game.wargame.AppConstant;
import com.game.wargame.Controller.Communication.Game.GameManagerSocket;
import com.game.wargame.Controller.Communication.IConnectionManager;
import com.game.wargame.R;

public class GameEntryFragment extends Fragment {

    private Callback mEntryCallback;
    private IConnectionManager mConnectionManager;


    public void setGameEntryCallbacks(GameEntryFragment.Callback callbacks) {
        mEntryCallback = callbacks;
    }

    public void setConnectionManager(IConnectionManager connectionManager) {
        mConnectionManager = connectionManager;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.game_entry, container, false);

        initView(fragment);

        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void initView(View view) {
        Button createGameButton = (Button) view.findViewById(R.id.new_game_button);
        Button joinGameButton = (Button) view.findViewById(R.id.join_button);

        final EditText userNameEditText = (EditText) view.findViewById(R.id.username_edittext);

        createGameButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final String username = userNameEditText.getText().toString();

                if (!username.isEmpty()) {
                    createGame();
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
                    joinGame(gameId);
                } else {
                    displayAlertDialog();
                }
            }
        });
    }

    private void createGame() {
        GameManagerSocket gameManagerSocket = mConnectionManager.getSocketFactory().buildGameManagerSocket();

        gameManagerSocket.createGame(new GameManagerSocket.OnGameCreatedListener() {
            @Override
            public void onGameCreated(String gameId) {
                joinGame(gameId);
                mEntryCallback.onGameCreated(gameId);
            }
        });
    }

    private void joinGame(final String gameId) {
        GameManagerSocket gameManagerSocket = mConnectionManager.getSocketFactory().buildGameManagerSocket();
        gameManagerSocket.joinGame(gameId, new GameManagerSocket.OnGameJoinedListener() {
            @Override
            public void onGameJoined(String playerId) {
                mEntryCallback.onPlayerJoined(gameId, playerId);
            }
        });
    }

    private void displayAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this.getActivity());

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


    public interface Callback {
        public void onGameCreated(String gameId);
        public void onPlayerJoined(String gameId, String playerId);
    }
}
