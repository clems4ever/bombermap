package com.game.wargame.Views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.game.wargame.Controller.GameLogic.GameScore;
import com.game.wargame.R;

/**
 * Created by sergei on 30/04/16.
 */
public class ScoreListViewAdapter extends ArrayAdapter<GameScore> {

    public ScoreListViewAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        GameScore score = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.score_holder, parent, false);
        }
        TextView tvName = (TextView) convertView.findViewById(R.id.score_name);
        TextView tvScore = (TextView) convertView.findViewById(R.id.score_text);
        tvName.setText(score.getPlayerId());
        tvScore.setText(score.getFrags()+"");
        return convertView;
    }
}
