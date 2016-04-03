package com.game.wargame.Model.GameContext;

import com.game.wargame.Controller.GameLogic.GameScore;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by sergei on 15/03/16.
 */

public class FragManager {

    private Map<String, GameScore> mScoresByPlayerId;

    public FragManager(Set<String> playersId){
        mScoresByPlayerId = new HashMap<>();
        for (String id : playersId) {
            mScoresByPlayerId.put(id, new GameScore());
        }
    }

    public void addFrag(String playerId) {
        GameScore gameScore = mScoresByPlayerId.get(playerId);
        gameScore.setFrags(gameScore.getFrags()+1);
    }

    public void addDeath(String playerId) {
        GameScore gameScore = mScoresByPlayerId.get(playerId);
        gameScore.setDeaths(gameScore.getDeaths() + 1);
    }
}
