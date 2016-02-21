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

import com.game.wargame.R;

public class MainMenuActivity extends Activity {

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        mContext = this;

        Button createGameButton = (Button) findViewById(R.id.new_game_button);
        Button joinGameButton = (Button) findViewById(R.id.join_button);

        final EditText userNameEditText = (EditText) findViewById(R.id.username_edittext);

        createGameButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final String username = userNameEditText.getText().toString();

                if (!username.isEmpty()) {
                    startGame(username, "create");
                } else {
                    displayAlertDialog();
                }
            }
        });

        joinGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = userNameEditText.getText().toString();

                if (!username.isEmpty()) {
                    startGame(username, "join");
                } else {
                    displayAlertDialog();
                }
            }
        });
    }

    private void startGame(String username, String type) {
        Intent intent = new Intent(mContext, MapActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("type", type);
        startActivity(intent);
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
    }


    @Override
    protected void onStop() {
        super.onStop();
    }
}
