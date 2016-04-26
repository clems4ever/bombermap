package com.game.wargame;

import com.google.android.gms.maps.model.LatLng;

public class AppConstant {

    public static boolean COMMUNICATION_DUMP_ENABLED = true;
    public static int RABBITMQ_HEARTBEAT = 6000;

    public static int GAME_DURATION = 60*5;

    public static String HOST = "broker.wargame.ingenious-cm.fr";
    public static String VIRTUAL_HOST = "/";

    public static double INITIAL_LONGITUDE = 48.8870407;
    public static double INITIAL_LATITUDE = 2.3228797;

    public static LatLng LAFOURCHE_LATLNG = new LatLng(48.8870407, 2.3228797);

    public static final boolean DEMO = true;

    public static String PLAYER_SCENARIO = "[" +
            "{\"lat\":48.886498921559465,\"long\":2.3225080221891403}," +
            "{\"lat\":48.8865743194227,\"long\":2.3226119577884674}," +
            "{\"lat\":48.88665192178312,\"long\":2.322676330804825}," +
            "{\"lat\":48.88674120844086,\"long\":2.32279434800148}," +
            "{\"lat\":48.886795221279854,\"long\":2.322869449853897}," +
            "{\"lat\":48.8869105219614,\"long\":2.322969362139702}," +
            "{\"lat\":48.88697158932749,\"long\":2.3230981081724167}," +
            "{\"lat\":48.887077409857476,\"long\":2.323230542242527}," +
            "{\"lat\":48.88713164279233,\"long\":2.3233378306031227}," +
            "{\"lat\":48.88718808025556,\"long\":2.3233985155820847}," +
            "{\"lat\":48.887274940914324,\"long\":2.3234987631440163}," +
            "{\"lat\":48.88735254218745,\"long\":2.323634549975395}," +
            "{\"lat\":48.88745130727001,\"long\":2.3237596079707146}," +
            "{\"lat\":48.88751722403574,\"long\":2.3239171877503395}" +
            "]";

    public static int getNumeroFromName(String name) {
        switch(name) {
            case "Serg":
                return 1;
            case "Clem":
                return 2;
            case "Elvis":
                return 3;
            case "Jose":
                return 4;
        }
        return 0;
    }
}
