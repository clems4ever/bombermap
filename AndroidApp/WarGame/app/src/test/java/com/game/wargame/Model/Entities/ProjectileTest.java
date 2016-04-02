package com.game.wargame.Model.Entities;

import com.game.wargame.Controller.GameLogic.OnExplosionListener;
import com.game.wargame.Model.Entities.Projectile;
import com.google.android.gms.maps.model.LatLng;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

import org.mockito.runners.MockitoJUnitRunner;

import java.util.TreeMap;

/**
 * Created by sergei on 20/03/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class ProjectileTest {

    @Mock
    private OnExplosionListener mExplosionListener;

    private Projectile initProjectile() {
        Projectile projectile = new Projectile("player_id", new LatLng(0,0), new LatLng(0,0), 0);
        projectile.setOnExplosionListener(mExplosionListener);
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
        projectile.update(1, 100);
        assertEquals(projectile.getPosition(), target);
    }

    @Test
    public void testThatProjectileExplodes() {
        Projectile projectile = initProjectile();

        TreeMap<Double, LatLng> trajectory = new TreeMap<>();
        LatLng start = new LatLng(0, 0);

        trajectory.put(1., start);
        projectile.setTrajectory(trajectory);

        projectile.update(1, 100);
        verify(mExplosionListener).onExplosion(projectile, 100);
    }
}
