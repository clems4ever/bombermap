package com.game.wargame.Controller.GameLogic;

import com.game.wargame.Controller.Communication.Game.LocalPlayerSocket;
import com.game.wargame.Controller.Utils.IDistanceCalculator;
import com.game.wargame.Model.Entities.EntitiesContainer;
import com.game.wargame.Model.Entities.Explosion;
import com.game.wargame.Model.Entities.Players.LocalPlayerModel;
import com.game.wargame.Model.Entities.Projectiles.Projectile;
import com.google.android.gms.maps.model.LatLng;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static junit.framework.Assert.assertEquals;

/**
 * Created by sergei on 20/03/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class CollisionTest {

    LocalPlayerModel mPlayerModel;
    EntitiesContainer mEntitiesContainer;
    CollisionManager mCollisionManager;

    @Mock
    LocalPlayerSocket mLocalPlayerSocket;

    public class DummyLocation implements IDistanceCalculator {
        @Override
        public void distanceBetween(double latitude1, double longitude1, double latitude2, double longitude2, float[] results) {
            if (results.length>0) {
                if (Math.abs(latitude1-latitude2)+Math.abs(longitude1-longitude2)>10)
                    results[0] = 100;
                else
                    results[0] = 0;
            }
        }
    }

    @Before
    public void setUp() {
        mPlayerModel = new LocalPlayerModel("player_id", mLocalPlayerSocket);

        mEntitiesContainer = new EntitiesContainer();
        mEntitiesContainer.addExplosion(new Explosion("other", 0, new LatLng(50, 50), 0));

        mCollisionManager = new CollisionManager(new DummyLocation());
    }

    @Test
    public void testThatTooFarFromExplosionToDie() {
        mPlayerModel.setPosition(new LatLng(60, 60));

        mCollisionManager.treatLocalPlayerAndExplosionCollision(mPlayerModel, mEntitiesContainer.getExplosions(), 100);
        assertEquals(100, mPlayerModel.getHealth());
    }

    @Test
    public void testThatCollisionKillsPlayer() {
        mPlayerModel.setPosition(new LatLng(55, 55));

        mCollisionManager.treatLocalPlayerAndExplosionCollision(mPlayerModel, mEntitiesContainer.getExplosions(), 100);
        assertEquals(0, mPlayerModel.getHealth());
    }

}
