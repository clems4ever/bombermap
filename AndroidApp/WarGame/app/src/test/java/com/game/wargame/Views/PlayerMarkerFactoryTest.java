package com.game.wargame.Views;

import com.game.wargame.Views.Animations.Size;
import com.game.wargame.Views.Bitmaps.BitmapDescriptorDescriptorFactory;
import com.game.wargame.Views.Bitmaps.IBitmapDescriptorFactory;
import com.game.wargame.Views.GoogleMap.GoogleMap;
import com.google.android.gms.maps.model.MarkerOptions;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.verify;

/**
 * Created by clement on 03/04/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class PlayerMarkerFactoryTest {

    @Mock private PlayerMarker mMockPlayerMarker;
    @Mock private GoogleMap mMockGoogleMap;
    @Mock private IBitmapDescriptorFactory mMockBitmapDescriptorFactory;

    @Test
    public void create_a_player_marker_on_map() {
        PlayerMarkerFactory factory = new PlayerMarkerFactory(mMockBitmapDescriptorFactory);
        factory.setGoogleMap(mMockGoogleMap);

        factory.create(4);

        verify(mMockBitmapDescriptorFactory).load(anyInt(), Matchers.<Size>any());
        verify(mMockGoogleMap).addPlayerMarker(Matchers.<MarkerOptions>any());
    }

}
