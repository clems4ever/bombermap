package com.game.wargame.Controller.Communication.Game;

import com.game.wargame.Views.GameView;

/**
 * Created by clement on 26/02/16.
 */
public class RemotePlayerSocket extends PlayerSocket {

    private RemotePlayersSocket mRemotePlayersSocket;

    private OnMoveEventListener mOnMoveEventListener;
    private OnFireEventListener mOnFireEventListener;
    private OnDieEventListener mOnDieEventListener;
    private OnRespawnEventListener mOnRespawnEventListener;
    private OnShieldEventListener mOnShieldEventListener;

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

    public void setOnDieEventListener(OnDieEventListener onDieEventListener) {
        mOnDieEventListener = onDieEventListener;
    }

    public void setOnRespawnEventListener(OnRespawnEventListener onRespawnEventListener) {
        mOnRespawnEventListener = onRespawnEventListener;
    }

    public void setOnShieldEventListener(OnShieldEventListener onShieldEventListener) {
        mOnShieldEventListener = onShieldEventListener;
    }

   public void onMove(double latitude, double longitude) {
        if(mOnMoveEventListener != null) {
            mOnMoveEventListener.onMoveEvent(latitude, longitude);
        }
   }

    public void onFire(double latitude, double longitude, double time) {
        if(mOnFireEventListener != null) {
            mOnFireEventListener.onFireEvent(latitude, longitude, time);
        }
    }

    public void onDie(String killerId, double time) {
        if(mOnDieEventListener != null) {
            mOnDieEventListener.onDieEvent(this.getPlayerId(), killerId, time);
        }
    }

    public void onRespawn(double time) {
        if (mOnRespawnEventListener != null)
            mOnRespawnEventListener.onRespawnEvent(this.getPlayerId(), time);
    }

    public void onShield(String playerId, boolean isShielded, double time) {
        if (mOnShieldEventListener != null)
            mOnShieldEventListener.onShieldEvent(playerId, isShielded, time);
    }

    public interface OnMoveEventListener {
        public void onMoveEvent(double latitude, double longitude);
    }

    public interface OnFireEventListener {
        public void onFireEvent(double latitude, double longitude, double velocity);
    }

    public interface OnDieEventListener {
        public void onDieEvent(String playerId, String killerId, double time);
    }

    public interface OnRespawnEventListener {
        public void onRespawnEvent(String playerId, double time);
    }

    public interface OnShieldEventListener {
        public void onShieldEvent(String playerId, boolean isShielded, double time);
    }
}
