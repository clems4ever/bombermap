package com.game.wargame.Views.Activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.game.wargame.R;
import com.game.wargame.Views.BitmapDescriptorFactory;
import com.game.wargame.Views.BundleExtractor;
import com.game.wargame.Views.GoogleMap.GoogleMapViewFactory;
import com.game.wargame.Views.GoogleMap.IGoogleMapView;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by clement on 03/04/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class GameMainFragmentTest {

    @Mock private LayoutInflater mMockLayoutInflater;
    @Mock private ViewGroup mMockViewGroup;
    @Mock private Bundle mMockBundle;
    @Mock private View mMockView;
    @Mock private View mMockOtherViews;
    @Mock private GoogleMapViewFactory mMockGoogleMapViewFactory;
    @Mock private IGoogleMapView mMockGoogleMapView;
    @Mock private BitmapDescriptorFactory mMockBitmapDescriptorFactory;
    @Mock private Button mMockButton;
    @Mock private BundleExtractor mMockBundleExtractor;

    @Test
    public void get_player_id_and_game_id_from_bundle_args() {
        when(mMockLayoutInflater.inflate(anyInt(), Matchers.<ViewGroup>any(), anyBoolean())).thenReturn(mMockView);
        when(mMockGoogleMapViewFactory.create(Matchers.<View>any())).thenReturn(mMockGoogleMapView);
        when(mMockView.findViewById(Matchers.eq(R.id.fire_button))).thenReturn(mMockButton);
        when(mMockBundleExtractor.getBundle()).thenReturn(mMockBundle);
        when(mMockBundle.getString(Matchers.eq("player_id"))).thenReturn("#playerid");
        when(mMockBundle.getString(Matchers.eq("game_id"))).thenReturn("#playerid");

        GameMainFragment gameMainFragment = new GameMainFragment(mMockGoogleMapViewFactory, mMockBitmapDescriptorFactory, mMockBundleExtractor);

        gameMainFragment.onCreateView(mMockLayoutInflater, mMockViewGroup, mMockBundle);

        verify(mMockBundle).getString(Matchers.eq("player_id"));
        verify(mMockBundle).getString(Matchers.eq("game_id"));
    }
}
