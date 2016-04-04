package com.game.wargame.Views.Activities;


import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.game.wargame.Controller.GameLogic.GameScore;
import com.game.wargame.Model.GameContext.GameContext;
import com.game.wargame.R;
import com.game.wargame.Views.GameView;

import org.w3c.dom.Text;

import java.util.Map;
import java.util.Set;

/**
 * Created by sergei on 03/04/16.
 */
public class ScoreBoardFragment extends Fragment {

    GameContext mGameContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.game_entry, container, false);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();

        Map<String, GameScore> scores = mGameContext.getScores();

        LinearLayout scoreLayout =  (LinearLayout) getActivity().findViewById(R.id.score_holder);

        Set<String> playersId = scores.keySet();
        for (String playerId : playersId) {
            TextView scoreTextView = new TextView(getActivity());
            scoreTextView.setText(scores.get(playerId).toString());
            scoreLayout.addView(scoreTextView);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public void setGameContext(GameContext gameContext) {
        mGameContext = gameContext;
    }
}
