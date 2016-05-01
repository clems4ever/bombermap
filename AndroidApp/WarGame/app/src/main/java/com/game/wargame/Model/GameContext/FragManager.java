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

    public FragManager() {
        mScoresByPlayerId = new HashMap<>();
    }

    public void addPlayer(String playerId) {
        mScoresByPlayerId.put(playerId, new GameScore(playerId));
    }

    public void addFrag(String id) {
        GameScore gameScore = mScoresByPlayerId.get(id);
        if (gameScore != null)
            gameScore.setFrags(gameScore.getFrags()+1);
    }

    public void addDeath(String id) {
        GameScore gameScore = mScoresByPlayerId.get(id);
        if (gameScore != null)
            gameScore.setDeaths(gameScore.getDeaths() + 1);
    }

    public Map<String, GameScore> getScores() {
        return mScoresByPlayerId;
    }
}
