package com.game.wargame.Controller.Communication.Game;

import com.game.wargame.Controller.Communication.ISocket;
import com.game.wargame.Controller.Communication.ISocketFactory;

import org.json.JSONException;
import org.json.JSONObject;

public class GameSocket {

    private String mGameId;
    private ISocket mSocket;
    private ISocketFactory mSocketFactory;

    private OnPlayerEventListener mOnPlayerEventListener;

    public GameSocket(String gameId, final ISocket socket, ISocketFactory socketFactory) {
        mGameId = gameId;
        mSocket = socket;
        mSocketFactory = socketFactory;

        mSocket.on("player_join", new ISocket.OnRemoteEventReceivedListener() {
            @Override
            public void onRemoteEventReceived(JSONObject message) {
                try {
                    String playerId = message.getString("player_id");
                    if(mOnPlayerEventListener != null) {
                        RemotePlayerSocket playerSocket = mSocketFactory.buildRemotePlayerSocket(mGameId, playerId);
                        mOnPlayerEventListener.onPlayerJoined(playerSocket);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        mSocket.on("player_join_ack", new ISocket.OnRemoteEventReceivedListener() {
            @Override
            public void onRemoteEventReceived(JSONObject message) {
                try {
                    String playerId = message.getString("player_id");
                    if(mOnPlayerEventListener != null) {
                        RemotePlayerSocket playerSocket = mSocketFactory.buildRemotePlayerSocket(mGameId, playerId);
                        mOnPlayerEventListener.onPlayerJoinAckReceived(playerSocket);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        mSocket.on("player_leave", new ISocket.OnRemoteEventReceivedListener() {
            @Override
            public void onRemoteEventReceived(JSONObject message) {
                try {
                    String playerId = message.getString("player_id");
                    if(mOnPlayerEventListener != null) {
                        RemotePlayerSocket playerSocket = mSocketFactory.buildRemotePlayerSocket(mGameId, playerId);
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
        public void onPlayerJoinAckReceived(RemotePlayerSocket playerSocket);
        public void onPlayerLeft(RemotePlayerSocket playerSocket);
    }
}