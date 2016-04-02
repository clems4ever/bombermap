package com.game.wargame.Controller.Engine;

import com.game.wargame.Model.GameContext;
import com.game.wargame.Model.Entities.EntitiesModel;

/**
 * Created by sergei on 14/03/16.
 */
public interface IUpdateCallback {
    public void update(EntitiesModel entities, long ticks, int increment);
    public void update(GameContext gameContext, long ticks, int increment);
}
