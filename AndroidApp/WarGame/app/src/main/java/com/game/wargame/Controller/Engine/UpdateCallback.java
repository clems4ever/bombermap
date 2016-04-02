package com.game.wargame.Controller.Engine;

import com.game.wargame.Model.GameContext.GameContext;
import com.game.wargame.Model.Entities.EntitiesModel;
import com.game.wargame.Model.Entities.Entity;

import java.util.ArrayList;

/**
 * Created by sergei on 14/03/16.
 */
public class UpdateCallback implements IUpdateCallback {

    public UpdateCallback() {}

    public void update(EntitiesModel entitiesModel, long ticks, int increment)
    {
        ArrayList<Entity> entities = entitiesModel.getEntities();
        for (Entity entity : entities)
        {
            entity.update(ticks, increment);
        }
    }

    public void update(GameContext gameContext, long ticks, int increment) {
        gameContext.update(ticks, increment);
    }
}
