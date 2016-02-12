package com.game.wargame.Activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.game.wargame.Communication.RemoteCommunicationService;
import com.game.wargame.Communication.RemoteCommunicationServiceConnection;
import com.game.wargame.Communication.RemoteCommunicationSystem;
import com.game.wargame.Entities.Player;
import com.game.wargame.Model.PlayerListAdapter;
import com.game.wargame.R;

import java.util.ArrayList;
import java.util.List;

public class RoomActivity extends AppCompatActivity {

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

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
            Button playButton = (Button) findViewById(R.id.play_button);
            final ListView playerListView = (ListView) findViewById(R.id.player_listview);
            final PlayerListAdapter adapter = new PlayerListAdapter(mContext);
            playerListView.setAdapter(adapter);

            // Get the current user
            Intent myIntent = getIntent();
            String username = myIntent.getStringExtra("username");
            String playerId = myIntent.getStringExtra("player_id");

            Player player = new Player(playerId, username);
            adapter.add(player);

            playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mConnection.getService().getRemoteCommunicationSystem().setOnGameStartedListener(new RemoteCommunicationSystem.OnGameStartedListener() {
                        @Override
                        public void onGameStarted() {
                            Intent intent = new Intent(mContext, MapActivity.class);
                            startActivity(intent);
                        }
                    });
                    mConnection.getService().getRemoteCommunicationSystem().startGame();
                }
            });

            mConnection.getService().getRemoteCommunicationSystem().setOnPlayerJoinedListener(new RemoteCommunicationSystem.OnPlayerJoinedListener() {
                @Override
                public void onPlayerJoined(String playerId, String name) {
                    adapter.add(new Player(playerId, name));
                }
            });

        }
    });
}
