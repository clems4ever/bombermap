package com.game.wargame.Views.WeaponController;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public abstract class AbstractWeaponControllerView extends RelativeLayout {

    private Context mContext;
    private OnActionFinishedListener mOnActionFinishedListener;

    public AbstractWeaponControllerView(Context context) {
        super(context);

        init(context);
    }

    public AbstractWeaponControllerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public AbstractWeaponControllerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context);
    }

    private void init(Context context) {
        mContext = context;
    }

    public abstract void initialize();
    public abstract void finalize();

    public void setOnActionFinishedListener(OnActionFinishedListener onActionFinishedListener) {
        mOnActionFinishedListener = onActionFinishedListener;
    }

    protected void emitOnActionFinished() {
        if(mOnActionFinishedListener != null) {
            mOnActionFinishedListener.onActionFinished();
        }
    }

    public interface OnActionFinishedListener {
        public void onActionFinished();
    }
}
