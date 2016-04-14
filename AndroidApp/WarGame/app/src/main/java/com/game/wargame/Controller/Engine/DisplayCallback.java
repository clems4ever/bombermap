package com.game.wargame.Controller.Engine;

import com.game.wargame.Model.Entities.EntitiesModel;
import com.game.wargame.Model.Entities.Players.LocalPlayerModel;
import com.game.wargame.Model.Entities.Players.Player;
import com.game.wargame.Model.Entities.Players.RemotePlayerModel;
import com.game.wargame.Model.GameContext.GameContext;
import com.game.wargame.Views.GameView;

import java.util.ArrayList;

/**
 * Created by sergei on 05/04/16.
 */
public class DisplayCallback implements IDisplayCallback {

    private EntitiesModel mEntities;
    private LocalPlayerModel mCurrentPlayer;
    private GameView mGameView;
    private GameContext mGameContext;
    private ArrayList<RemotePlayerModel> mRemotePlayers;

    public DisplayCallback(GameView gameView, GameContext gameContext, LocalPlayerModel currentPlayer, EntitiesModel entities) {
        mGameView = gameView;
        mGameContext = gameContext;
        mCurrentPlayer = currentPlayer;
        mEntities = entities;
        mRemotePlayers = new ArrayList<>();
    }

    public void addRemotePlayer(RemotePlayerModel remotePlayerModel) {
        mRemotePlayers.add(remotePlayerModel);
    }

    @Override
    public void display() {
        mGameView.display(mCurrentPlayer);
        mGameView.display(mGameContext);
        mGameView.displayProjectiles(mEntities.getProjectiles());
        mGameView.displayExplosions(mEntities.getExplosions());
        mGameView.displayCells(mEntities.getRealCells());
        for (RemotePlayerModel remotePlayerModel : mRemotePlayers) {
            mGameView.display(remotePlayerModel);
        }
    }
}
