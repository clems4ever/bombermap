package com.game.wargame.Activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.game.wargame.Communication.RemoteCommunicationService;
import com.game.wargame.Communication.RemoteCommunicationServiceConnection;
import com.game.wargame.Communication.GameEngineSocket;
import com.game.wargame.GameEngine.GameEngine;
import com.game.wargame.R;
import com.game.wargame.WarGameApplication;

public class MainMenuActivity extends AppCompatActivity {

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

                Intent intent = new Intent(mContext, MapActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("type", "create");
                startActivity(intent);
            }
        });

        joinGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = userNameEditText.getText().toString();
                Intent intent = new Intent(mContext, MapActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("type", "join");
                startActivity(intent);
            }
        });
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
