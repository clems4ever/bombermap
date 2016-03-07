package com.game.wargame.Controller.Communication.Game;

/**
 * Created by clement on 26/02/16.
 */
public class RemotePlayerSocket extends PlayerSocket {

    private RemotePlayersSocket mRemotePlayersSocket;

    private OnMoveEventListener mOnMoveEventListener;
    private OnFireEventListener mOnFireEventListener;

    /**
     * @param playerId
     */
    public RemotePlayerSocket(String playerId, RemotePlayersSocket remotePlayersSocket) {
        super(playerId);
        mRemotePlayersSocket = remotePlayersSocket;
        remotePlayersSocket.addPlayer(this);
    }

    public void setOnMoveEventListener(OnMoveEventListener onMoveEventListener) {
        mOnMoveEventListener = onMoveEventListener;
    }

    public void setOnFireEventListener(OnFireEventListener onFireEventListener) {
        mOnFireEventListener = onFireEventListener;
    }

   public void onMove(double latitude, double longitude) {
        if(mOnMoveEventListener != null) {
            mOnMoveEventListener.onMoveEvent(latitude, longitude);
        }
   }

    public void onFire(double latitude, double longitude, double speed) {
        if(mOnFireEventListener != null) {
            mOnFireEventListener.onFireEvent(latitude, longitude, speed);
        }
    }

    public interface OnMoveEventListener {
        public void onMoveEvent(double latitude, double longitude);
    }

    public interface OnFireEventListener {
        public void onFireEvent(double latitude, double longitude, double velocity);
    }
}
