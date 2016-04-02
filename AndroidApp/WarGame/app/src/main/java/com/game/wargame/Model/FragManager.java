package com.game.wargame.Model;

import com.game.wargame.Controller.GameLogic.GameScore;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by sergei on 15/03/16.
 */

public class FragManager {

    private Map<String, GameScore> mScoresById;

    public FragManager(Set<String> playersId){
        mScoresById = new HashMap<>();
        for (String id : playersId) {
            mScoresById.put(id, new GameScore());
        }
    }

    public void addFrag(String id) {
        GameScore gameScore = mScoresById.get(id);
        gameScore.setFrags(gameScore.getFrags()+1);
    }

    public void addDeath(String id) {
        GameScore gameScore = mScoresById.get(id);
        gameScore.setDeaths(gameScore.getDeaths() + 1);
    }
}
