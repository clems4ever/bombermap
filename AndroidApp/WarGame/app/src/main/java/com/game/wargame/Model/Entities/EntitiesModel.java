package com.game.wargame.Model.Entities;

import com.game.wargame.Model.Entities.Entity;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by sergei on 01/03/16.
 */

public class EntitiesModel {

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


    protected Lock mLock = new ReentrantLock();
    protected ArrayList<Entity> mEntities = new ArrayList<>();
}
