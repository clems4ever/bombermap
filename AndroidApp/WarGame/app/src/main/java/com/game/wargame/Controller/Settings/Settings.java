package com.game.wargame.Controller.Settings;

/**
 * Created by clement on 10/04/16.
 */
public class Settings {

    public enum GameEngineMode { PRODUCTION, DEBUG, SCENARIO_REPLAYER }

    public GameEngineMode mode;
    public String virtualHost;
    public String hostname;

    public int gameDuration;

    // Default settings
    public Settings() {
        mode = GameEngineMode.PRODUCTION;
        hostname = "broker.wargame.ingenious-cm.fr";
        virtualHost = "/";
        gameDuration = 60 * 5;
    }



}
