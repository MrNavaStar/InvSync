package com.mrnavastar.invsync.util;

import com.github.underscore.lodash.U;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mrnavastar.invsync.Invsync;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;

public class ConfigManager {

    public static File configFile;

    public static String SQL_Database_Name, SQL_Database_Table_Name, SQL_Database_Directory;
    public static boolean Sync_Inv, Sync_Armour, Sync_eChest, Sync_Xp, Sync_Score, Sync_Health, Sync_Food_Level;

    //Set up config file
    public static void prepareConfigFile() {
        if (configFile != null) {
            return;
        }
        configFile = new File(FabricLoader.getInstance().getConfigDir().toString(), Invsync.MODID + "Config.json");
    }

    //Generate content for config file
    public static void createConfig() {
        JsonObject SQLConfigProperties = new JsonObject();
        SQLConfigProperties.addProperty("_comment_", "Settings for your database. THESE MUST BE THE SAME BETWEEN SERVERS. (Directory must be reachable by all servers)");
        SQLConfigProperties.addProperty("Database_Name", "database.db");
        SQLConfigProperties.addProperty("Database_Table_Name", "playerData");
        SQLConfigProperties.addProperty("Database_Directory", "/var/lib/databases");

        JsonObject SyncConfigProperties = new JsonObject();
        SyncConfigProperties.addProperty("_comment_", "Settings for what to sync between servers. THESE MUST BE THE SAME BETWEEN SERVERS.");
        SyncConfigProperties.addProperty("Sync_Inv", true);
        SyncConfigProperties.addProperty("Sync_Armour", true);
        SyncConfigProperties.addProperty("Sync_eChest", true);
        SyncConfigProperties.addProperty("Sync_Xp", true);
        SyncConfigProperties.addProperty("Sync_Score", true);
        SyncConfigProperties.addProperty("Sync_Health", true);
        SyncConfigProperties.addProperty("Sync_Food_Level", true);

        JsonArray invSyncProperties = new JsonArray();
        invSyncProperties.add(SQLConfigProperties);
        invSyncProperties.add(SyncConfigProperties);

        jsonWriter(invSyncProperties, configFile);
    }

    //Writes JSON array to config file
    public static void jsonWriter(JsonArray obj, File output) {
        try {
            FileWriter file = new FileWriter(output);
            file.write(U.formatJson(obj.toString()));
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Reads config file and returns JSON array
    public static String jsonReader(File input, String properties) {
        JsonParser parser = new JsonParser();
        String str = null;
        try {
            Object obj = parser.parse(new FileReader(input));
            JsonObject jsonObject = (JsonObject) obj;
            str = jsonObject.get(properties).toString();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static void loadConfig() {
        prepareConfigFile();
        if (!configFile.exists()) createConfig();
    }
}

