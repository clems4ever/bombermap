package com.game.wargame.Model.Players;

import com.game.wargame.AppConstant;
import com.game.wargame.Model.Entities.Players.Player;
import com.game.wargame.Model.Entities.Players.PlayerException;
import com.google.android.gms.maps.model.LatLng;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PlayerTest {


    @Test
    public void testPlayerSetters() throws PlayerException {
        Player player = new Player("player_id", "player_name");

        LatLng position = new LatLng(20, 30);

        player.setPlayerId("p1");
        player.setPlayerName("p2");
        player.setPosition(position);
        player.setHealth(50);

        assertEquals("p1", player.getPlayerId());
        assertEquals("p2", player.getPlayerName());
        assertEquals(position, player.getPosition());
        assertEquals(50, player.getHealth());
    }

    @Test(expected = PlayerException.class)
    public void testPlayerCannotHaveHealthLessThanZero() throws PlayerException {
        Player player = new Player("player_id", "player_name");

        player.setHealth(-10);
    }

    @Test(expected = PlayerException.class)
    public void testPlayerCannotHaveHealthGreaterThan100() throws PlayerException {
        Player player = new Player("player_id", "player_name");

        player.setHealth(110);
    }

    @Test
    public void testThatPlayerPositionIsCorrectlyInitialized() {
        Player player = new Player("p", "p");

        assertEquals(AppConstant.INITIAL_LATITUDE, player.getPosition().latitude, 0.00001);
        assertEquals(AppConstant.INITIAL_LONGITUDE, player.getPosition().longitude, 0.00001);

    }
}
