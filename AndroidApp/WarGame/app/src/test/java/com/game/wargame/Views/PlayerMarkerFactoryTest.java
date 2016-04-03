package com.game.wargame.Views;

import com.google.android.gms.maps.model.GroundOverlayOptions;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

/**
 * Created by clement on 03/04/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class PlayerMarkerFactoryTest {

    @Mock PlayerMarker mMockPlayerMarker;
    @Mock GoogleMap mMockGoogleMap;
    @Mock BitmapDescriptorFactory mMockBitmapDescriptorFactory;

    @Test
    public void create_a_player_marker_on_map() {

        PlayerMarkerFactory factory = new PlayerMarkerFactory(mMockBitmapDescriptorFactory);
        factory.setGoogleMap(mMockGoogleMap);

        PlayerMarker playerMarker = factory.create(4);

        verify(mMockBitmapDescriptorFactory).load(4);
        verify(mMockGoogleMap).addPlayerMarker(Matchers.<GroundOverlayOptions>any());
    }

}
