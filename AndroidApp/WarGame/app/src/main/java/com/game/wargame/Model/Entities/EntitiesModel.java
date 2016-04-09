package com.game.wargame.Model.Entities;

import com.game.wargame.Model.Entities.Entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by sergei on 01/03/16.
 */

public class EntitiesModel implements Updatable {

    public EntitiesModel() {

    }

    public ArrayList<Entity> getEntities() {
        ArrayList<Entity> entities = new ArrayList<>();

        mLock.lock();
        entities.addAll(mEntities);
        mLock.unlock();

        return entities;
    }

    public void addEntity(Entity entity) {
        mLock.lock();
        mEntities.add(entity);
        mLock.unlock();
    }

    public void removeEntity(Entity entity) {
        mLock.lock();
        mEntities.remove(entity);
        mLock.unlock();
    }


    public void update(long ticks, int increment) {
        ArrayList<Entity> entities = new ArrayList<>();
        mLock.lock();
        entities.addAll(mEntities);
        for (Entity entity : entities) {
            entity.update(ticks, increment);
            if (entity.isToRemove() && !entity.isDisplayed())
                mEntities.remove(entity);
        }
        mLock.unlock();
    }

    public void setDisplayed(Entity entity, boolean isDisplayed) {
        mLock.lock();
        entity.setDisplayed(isDisplayed);
        mLock.unlock();
    }

    protected Lock mLock = new ReentrantLock();
    protected ArrayList<Entity> mEntities = new ArrayList<>();
}
