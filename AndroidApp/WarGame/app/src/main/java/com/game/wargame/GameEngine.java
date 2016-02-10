package com.game.wargame;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by clement on 09/02/16.
 */
public class GameEngine {

    private Context mContext;
    private List<Player> mPlayers;

    public GameEngine(Context context) {
        mContext = context;

        mPlayers = new ArrayList<>();
    }

    public void addPlayer(Player player) {
        mPlayers.add(player);
    }

}
