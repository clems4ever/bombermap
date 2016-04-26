package com.game.wargame.Views.Views;

import android.support.v4.app.FragmentActivity;
import android.text.format.DateUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.game.wargame.Controller.GameLogic.GameScore;
import com.game.wargame.Model.Entities.Entity;
import com.game.wargame.Model.Entities.Players.Player;
import com.game.wargame.Model.Entities.Players.PlayerModel;
import com.game.wargame.Model.Entities.VirtualMap.RealCell;
import com.game.wargame.Model.GameContext.GameContext;
import com.game.wargame.Model.GameContext.GameNotification;
import com.game.wargame.R;
import com.game.wargame.Views.Bitmaps.BitmapDescriptorDescriptorFactory;
import com.game.wargame.Views.GoogleMap.IGoogleMapView;
import com.game.wargame.Views.WeaponController.AbstractWeaponControllerView;
import com.game.wargame.Views.WeaponController.RocketControllerView;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Set;

public class GameView implements AbstractWeaponControllerView.OnActionFinishedListener {

    private FragmentActivity mActivity;
    private MapView mMapView;

    private RelativeLayout mMapLayout;
    private AbstractWeaponControllerView mWeaponControllerInterface;
    private ImageButton mFireButton;
    private ImageButton mShieldButton;
    private ImageButton mGpsButton;
    private TextView mTimeText;
    private TextView[] mNotificationsBuffer;
    private TextView[] mScoreBoard;

    private OnWeaponTargetDefinedListener mOnWeaponTargetDefined;
    private OnShieldListener mOnShieldListener;

    public GameView(final FragmentActivity activity, View view, IGoogleMapView googleMapView, BitmapDescriptorDescriptorFactory bitmapDescriptorFactory) {
        init(activity, view, googleMapView, bitmapDescriptorFactory);
    }

    private void init(final FragmentActivity activity, View view, IGoogleMapView googleMapView, BitmapDescriptorDescriptorFactory bitmapDescriptorFactory) {
        mActivity = activity;

        mMapView = new MapView(mActivity, googleMapView, bitmapDescriptorFactory);
        final GameView that = this;

        mFireButton = (ImageButton) view.findViewById(R.id.fire_button);
        mShieldButton = (ImageButton) view.findViewById(R.id.shield_button);
        mGpsButton = (ImageButton) view.findViewById(R.id.gps_button);
        mTimeText = (TextView) view.findViewById(R.id.time_text);

        mNotificationsBuffer = new TextView[3];
        mNotificationsBuffer[0] = (TextView) view.findViewById(R.id.game_notif_1);
        mNotificationsBuffer[1] = (TextView) view.findViewById(R.id.game_notif_2);
        mNotificationsBuffer[2] = (TextView) view.findViewById(R.id.game_notif_3);

        mScoreBoard = new TextView[3];
        mScoreBoard [0] = (TextView) view.findViewById(R.id.score1);
        mScoreBoard [1] = (TextView) view.findViewById(R.id.score2);
        mScoreBoard [2] = (TextView) view.findViewById(R.id.score3);

        mFireButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                that.setWeaponController(new RocketControllerView(activity));
            }
        });

        mShieldButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnShieldListener.onShield();
            }
        });

        mMapLayout = (RelativeLayout) view.findViewById(R.id.map_layout);
    }

    public void initialize(OnWeaponTargetDefinedListener onWeaponTargetDefinedListener, OnShieldListener onShieldListener) {
        mOnWeaponTargetDefined = onWeaponTargetDefinedListener;
        mOnShieldListener = onShieldListener;
    }

    public void start(MapView.OnMapReadyListener onMapReadyListener) {
        mMapView.startAsync(onMapReadyListener);
    }

    public void addLocalPlayer(final PlayerModel player) {
        mMapView.addLocalPlayer(player.getPlayerId());
    }

    public void addRemotePlayer(final PlayerModel player) {
        mMapView.addRemotePlayer(player.getPlayerId());
    }

    public void movePlayer(final PlayerModel player) {
        mMapView.movePlayerTo(player.getPlayerId(), player.getPosition());
    }

    public void removePlayer(PlayerModel player) {
        mMapView.removePlayer(player.getPlayerId());
    }

    public void moveCameraTo(LatLng position) {
        mMapView.moveCameraTo(position);
    }

    public void moveCameraTo(LatLng position, float zoom) {
        mMapView.moveCameraTo(position, zoom);
    }

    public void setWeaponController(AbstractWeaponControllerView weaponController) {

        if(mWeaponControllerInterface == null) {
            mWeaponControllerInterface = weaponController;
        }

        if(mWeaponControllerInterface != null) {
            mWeaponControllerInterface.initialize();
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);

            if (mWeaponControllerInterface.getParent() == null)
                mMapLayout.addView(mWeaponControllerInterface, params);
            mWeaponControllerInterface.setOnActionFinishedListener(this);
            mWeaponControllerInterface.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {

                        float targetX = event.getX();
                        float targetY = event.getY();

                        if (mOnWeaponTargetDefined != null) {
                            mOnWeaponTargetDefined.onWeaponTargetDefined(targetX, targetY);
                        }
                        onActionFinished();
                    }
                    return true;
                }
            });
            mMapLayout.invalidate();
        }
    }

    @Override
    public void onActionFinished() {
        if(mWeaponControllerInterface != null) {
            mWeaponControllerInterface.finalize();
            mMapLayout.removeView(mWeaponControllerInterface);
            mMapLayout.invalidate();
            mWeaponControllerInterface = null;
        }
    }

    public void display(GameContext gameContext) {
        String time = DateUtils.formatElapsedTime(gameContext.getRemainingTime());
        mTimeText.setText(time);

        ArrayList<GameNotification> gameNotifications = gameContext.getNotificationsToDisplay();

        for (int i=0; i<3; i++) {
            mNotificationsBuffer[i].setText("");
            if (i < gameNotifications.size())
                mNotificationsBuffer[i].setText(gameNotifications.get(i).getText());
        }

        java.util.Map<String, GameScore> scores = gameContext.getScores();
        Set<String> playersId = scores.keySet();
        int i = 0;
        for (String playerId : playersId)
        {
            GameScore gameScore = scores.get(playerId);
            
            if (i<3)
               mScoreBoard[i].setText(gameScore.toString());
            i++;
        }

        mMapView.display(gameContext);
    }

    public void display(Player player) {
        mMapView.display(player);
    }

    public void removeBlock(RealCell block) {
        mMapView.removeBlock(block);
    }

    public void addEntity(Entity e) {
        mMapView.addEntity(e);
    }

    public void updateEntity(Entity e) {
        mMapView.updateEntity(e);
    }

    public void removeEntity(Entity e) {
        mMapView.removeEntity(e);
    }

    public void setOnGpsButtonClickedListener(View.OnClickListener onGpsButtonClickedListener) {
        mGpsButton.setOnClickListener(onGpsButtonClickedListener);
    }

    public Projection getMapProjection() {
        return mMapView.getMapProjection();
    }

    public MapView getMapView() {
        return mMapView;
    }

    public interface OnWeaponTargetDefinedListener {
        public void onWeaponTargetDefined(float x, float y);
    }

    public interface OnShieldListener {
        public void onShield();
    }
}
