package com.game.wargame.Model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.game.wargame.Model.Entities.Players.Player;
import com.game.wargame.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by clement on 13/02/16.
 */
public class PlayerListAdapter extends BaseAdapter {

    private List<Player> mPlayers = new ArrayList<Player>();
    private LayoutInflater mLayoutInflater;

    public PlayerListAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mPlayers.size();
    }

    @Override
    public Object getItem(int position) {
        return mPlayers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mLayoutInflater.inflate(R.layout.player_row, null);

        TextView usernameTextView = (TextView) convertView.findViewById(R.id.username_textview);
        usernameTextView.setText(mPlayers.get(position).getPlayerName());

        return convertView;
    }

    public void add(Player player) {
        mPlayers.add(player);
    }
}
