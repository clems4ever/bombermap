package com.game.wargame.Model.Entities;

import com.game.wargame.Controller.Engine.DisplayCommands.AddExplosionDisplayCommand;
import com.game.wargame.Controller.Engine.DisplayCommands.RemoveProjectileDisplayCommand;
import com.game.wargame.Controller.Engine.DisplayTransaction;
import com.game.wargame.Controller.GameLogic.OnExplosionListener;
import com.game.wargame.Model.Entities.Projectiles.Projectile;
import com.google.android.gms.maps.model.LatLng;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.TreeMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by sergei on 20/03/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class ProjectileTest {

    @Mock private OnExplosionListener mExplosionListener;
    @Mock private DisplayTransaction mMockDisplayTransaction;
    @Mock private Projectile mMockProjectile;
    @Mock private EntitiesContainer mMockEntitiesContainer;

    private Projectile initProjectile() {
        Projectile projectile = new Projectile("player_id", new LatLng(0,0), new LatLng(0,0), 0);
        return projectile;
    }

    @Test
    public void testThatProjectileIsMovingAlongTrajectory() {
        Projectile projectile = initProjectile();

        TreeMap<Double, LatLng> trajectory = new TreeMap<>();
        LatLng start = new LatLng(0,0);
        LatLng target = new LatLng(100,100);

        trajectory.put(1., start);
        trajectory.put(100., target);
        projectile.setTrajectory(trajectory);

        assertEquals(projectile.getPosition(), start);
        projectile.update(1, 100, mMockEntitiesContainer, mMockDisplayTransaction);
        assertEquals(projectile.getPosition(), target);
    }

    @Test
    public void testThatProjectileExplodes() {

        Projectile projectile = initProjectile();

        TreeMap<Double, LatLng> trajectory = new TreeMap<>();
        LatLng start = new LatLng(0, 0);

        trajectory.put(1., start);
        projectile.setTrajectory(trajectory);

        projectile.update(1, 100, mMockEntitiesContainer, mMockDisplayTransaction);

        verify(mMockDisplayTransaction, times(1)).add(isA(RemoveProjectileDisplayCommand.class));
        verify(mMockDisplayTransaction, times(1)).add(isA(AddExplosionDisplayCommand.class));
    }
}
