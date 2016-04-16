package com.game.wargame.Model.Entities;

import com.game.wargame.Controller.Engine.DisplayTransaction;

import java.util.List;

/**
 * Created by sergei on 05/04/16.
 */
public interface Updatable {

    public void update(long ticks, int increment, EntitiesContainerUpdater entitiesContainerUpdater, DisplayTransaction displayTransaction);

}
