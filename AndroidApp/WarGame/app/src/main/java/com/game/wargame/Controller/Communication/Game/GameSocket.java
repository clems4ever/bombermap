package com.game.wargame.Controller.Communication.Game;

import com.game.wargame.Controller.Communication.IEventSocket;
import com.game.wargame.Controller.Communication.Socket;

import org.json.JSONException;
import org.json.JSONObject;

public class GameSocket {

    private String mGameId;
    private Socket mSocket;

    private OnPlayerEventListener mOnPlayerEventListener;

    public GameSocket(String gameId, final Socket socket) {
        mGameId = gameId;
        mSocket = socket;

        mSocket.on("player_join", new IEventSocket.OnRemoteEventReceivedListener() {
            @Override
            public void onRemoteEventReceived(JSONObject message) {
                try {
                    String playerId = message.getString("player_id");
                    if(mOnPlayerEventListener != null) {
                        RemotePlayerSocket playerSocket = socket.getConnectionManager().buildRemotePlayerSocket(mGameId, playerId);

                        mOnPlayerEventListener.onPlayerJoined(playerSocket);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        mSocket.on("player_join_ack", new IEventSocket.OnRemoteEventReceivedListener() {
            @Override
            public void onRemoteEventReceived(JSONObject message) {
                try {
                    String playerId = message.getString("player_id");
                    if(mOnPlayerEventListener != null) {
                        RemotePlayerSocket playerSocket = socket.getConnectionManager().buildRemotePlayerSocket(mGameId, playerId);

                        mOnPlayerEventListener.onPlayerJoinAckReceived(playerSocket);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        mSocket.on("player_leave", new IEventSocket.OnRemoteEventReceivedListener() {
            @Override
            public void onRemoteEventReceived(JSONObject message) {
                try {
                    String playerId = message.getString("player_id");
                    if(mOnPlayerEventListener != null) {
                        RemotePlayerSocket playerSocket = socket.getConnectionManager().buildRemotePlayerSocket(mGameId, playerId);
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
