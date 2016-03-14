package com.game.wargame.Views;

import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.game.wargame.Model.Entities.PlayerModel;
import com.game.wargame.Model.Entities.Projectile;
import com.game.wargame.R;
import com.game.wargame.Views.WeaponController.AbstractWeaponControllerView;
import com.game.wargame.Views.WeaponController.RocketControllerView;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;


public class GameView implements AbstractWeaponControllerView.OnActionFinishedListener {

    private FragmentActivity mActivity;
    private MapView mMapView;

    private RelativeLayout mMapLayout;
    private AbstractWeaponControllerView mWeaponControllerInterface;
    private Button mFireButton;
    private ImageButton mGpsButton;

    private OnWeaponTargetDefinedListener mOnWeaponTargetDefined;

    public GameView(final FragmentActivity activity) {
        com.google.android.gms.maps.MapView mapView = (com.google.android.gms.maps.MapView) mActivity.findViewById(R.id.map);

        mActivity = activity;
        mMapView = new MapView(mActivity, new GoogleMapViewWrapper(mapView), new BitmapDescriptorFactory());
        final GameView that = this;

        mFireButton = (Button) mActivity.findViewById(R.id.fire_button);
        mGpsButton = (ImageButton) mActivity.findViewById(R.id.gps_button);

        mFireButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                that.setWeaponController(new RocketControllerView(activity));
            }
        });

        mMapLayout = (RelativeLayout) activity.findViewById(R.id.map_layout);
    }

    public void initialize(OnWeaponTargetDefinedListener onWeaponTargetDefinedListener) {
        mOnWeaponTargetDefined = onWeaponTargetDefinedListener;
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
        mMapView.removePlayer(player);
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

                        if(mOnWeaponTargetDefined != null) {
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

    public void renderProjectiles(ArrayList<Projectile> projectiles) {
        mMapView.renderProjectiles(projectiles);
    }

    public void setOnGpsButtonClickedListener(View.OnClickListener onGpsButtonClickedListener) {
        mGpsButton.setOnClickListener(onGpsButtonClickedListener);
    }

    public Projection getMapProjection() {
        return mMapView.getMapProjection();
    }

    public interface OnWeaponTargetDefinedListener {
        public void onWeaponTargetDefined(float x, float y);
    }
}
