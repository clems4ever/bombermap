package com.game.wargame.Controller.Settings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Created by clement on 10/04/16.
 */
public class SettingsReader {

    public SettingsReader() {
    }

    public Settings read(String filename) throws IOException, JSONException {
        Settings settings = new Settings();

        FileInputStream fin = new FileInputStream(filename);

        String fileContent = new String();
        byte[] buffer = new byte[1024];
        while(fin.read(buffer) != -1) {
            fileContent += new String(buffer);
        }
        JSONObject document = new JSONObject(fileContent);

        if(document.has("game_duration")) {
            settings.gameDuration = document.getInt("game_duration");
        }

        if(document.has("virtual_host")) {
            settings.virtualHost = document.getString("virtual_host");
        }

        if(document.has("hostname")) {
            settings.hostname = document.getString("hostname");
        }

        if(document.has("mode")) {
            settings.mode = convertStringIntoGameEngineMode(document.getString("mode"));
        }

        if(document.has("player_scenarii")) {
            JSONArray scenarii = document.getJSONArray("player_scenarii");

            if(document.has("selected_player_scenario")) {
                int index = document.getInt("selected_player_scenario");

                if(index < scenarii.length()) {
                    settings.playerScenario = scenarii.getJSONArray(index);
                }
            }
        }

        return settings;
    }


    public static Settings.GameEngineMode convertStringIntoGameEngineMode(String str) {
        Settings.GameEngineMode mode = Settings.GameEngineMode.PRODUCTION;

        if(str.equals("SCENARIO_REPLAYER")) {
            mode = Settings.GameEngineMode.SCENARIO_REPLAYER;
        }
        else if(str.equals("DEBUG")) {
            mode = Settings.GameEngineMode.DEBUG;
        }
        else if(str.equals("MANUAL")) {
            mode = Settings.GameEngineMode.MANUAL;
        }

        return mode;
    }


}
