package com.game.wargame.Controller.Engine;

import com.game.wargame.Model.Entities.EntitiesModel;
import com.game.wargame.Model.Entities.Entity;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by sergei on 14/03/16.
 */
public class EntitiesUpdateCallback implements IUpdateCallback {

    public EntitiesUpdateCallback() {}

    public void update(EntitiesModel entitiesModel, long ticks, int increment)
    {
        ArrayList<Entity> entities = entitiesModel.getEntities();
        for (Entity entity : entities)
        {
            entity.update(ticks, increment);
        }
    }
}
