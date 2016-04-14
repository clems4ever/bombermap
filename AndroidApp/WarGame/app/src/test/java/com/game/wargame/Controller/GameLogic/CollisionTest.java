package com.game.wargame.Controller.GameLogic;

import com.game.wargame.Controller.Communication.Game.LocalPlayerSocket;
import com.game.wargame.Controller.Utils.IDistanceCalculator;
import com.game.wargame.Model.Entities.EntitiesModel;
import com.game.wargame.Model.Entities.Explosion;
import com.game.wargame.Model.Entities.Players.LocalPlayerModel;
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
    EntitiesModel mEntitiesModel;
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

        mEntitiesModel = new EntitiesModel();
        Explosion explosion = new Explosion("other", 100, new LatLng(50,50), 0);
        mEntitiesModel.addEntity(explosion);

        mCollisionManager = new CollisionManager(new DummyLocation());
    }

    @Test
    public void testThatTooFarFromExplosionToDie() {
        mPlayerModel.setPosition(new LatLng(60, 60));

        mCollisionManager.treatPlayerEntitiesCollisions(mEntitiesModel, mPlayerModel, 100);
        assertEquals(100, mPlayerModel.getHealth());
    }

    @Test
    public void testThatCollisionKillsPlayer() {
     mPlayerModel.setPosition(new LatLng(51, 51));

        mCollisionManager.treatPlayerEntitiesCollisions(mEntitiesModel, mPlayerModel, 100);
        assertEquals(0, mPlayerModel.getHealth());
    }

}
