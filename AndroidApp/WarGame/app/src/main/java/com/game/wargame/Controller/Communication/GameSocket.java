package com.game.wargame.Controller.Communication;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by clement on 27/02/16.
 */
public class GameSocket {

    private String mGameId;
    private IEventSocket mSocket;

    private IConnectionManager mConnectionManager;

    private OnPlayerEventListener mOnPlayerEventListener;

    public GameSocket(String gameId, IEventSocket socket, IConnectionManager connectionManager) {
        mGameId = gameId;
        mSocket = socket;
        mConnectionManager = connectionManager;

        mSocket.on("on_join", new IEventSocket.OnRemoteEventReceivedListener() {
            @Override
            public void onRemoteEventReceived(JSONObject message) {
                try {
                    String playerId = message.getString("player_id");
                    if(mOnPlayerEventListener != null) {
                        RemotePlayerSocket playerSocket = mConnectionManager.buildRemotePlayerSocket(mGameId, playerId);

                        mOnPlayerEventListener.onPlayerJoined(playerSocket);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        mSocket.on("on_leave", new IEventSocket.OnRemoteEventReceivedListener() {
            @Override
            public void onRemoteEventReceived(JSONObject message) {
                try {
                    String playerId = message.getString("player_id");
                    if(mOnPlayerEventListener != null) {
                        RemotePlayerSocket playerSocket = mConnectionManager.buildRemotePlayerSocket(mGameId, playerId);
                        mOnPlayerEventListener.onPlayerLeft(playerSocket);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public String getGameId() {
        return mGameId;
    }

    public void setOnPlayerEventListener(OnPlayerEventListener onPlayerEventListener) {
        mOnPlayerEventListener = onPlayerEventListener;
    }


    public interface OnPlayerEventListener {
        public void onPlayerJoined(RemotePlayerSocket playerSocket);
        public void onPlayerLeft(RemotePlayerSocket playerSocket);
    }
}
