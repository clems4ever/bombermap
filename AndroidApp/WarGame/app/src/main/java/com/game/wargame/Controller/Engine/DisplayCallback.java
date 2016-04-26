package com.game.wargame.Controller.Engine;

import com.game.wargame.AppConstant;
import com.game.wargame.Model.Entities.EntitiesContainer;
import com.game.wargame.Model.Entities.Players.LocalPlayerModel;
import com.game.wargame.Model.Entities.Players.RemotePlayerModel;
import com.game.wargame.Model.GameContext.GameContext;
import com.game.wargame.Views.Views.GameView;

import java.util.ArrayList;

/**
 * Created by sergei on 05/04/16.
 */
public class DisplayCallback implements IDisplayCallback {

    private EntitiesContainer mEntities;
    private LocalPlayerModel mCurrentPlayer;
    private GameView mGameView;
    private GameContext mGameContext;
    private ArrayList<RemotePlayerModel> mRemotePlayers;

    public DisplayCallback(GameView gameView, GameContext gameContext, LocalPlayerModel currentPlayer, EntitiesContainer entities) {
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
        if (!AppConstant.DEMO)
            mGameView.display(mCurrentPlayer);
        mGameView.display(mGameContext);
        for (RemotePlayerModel remotePlayerModel : mRemotePlayers) {
            mGameView.display(remotePlayerModel);
        }
    }
}
