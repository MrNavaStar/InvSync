<<<<<<< HEAD
package com.mrnavastar.invsync.util;

import com.github.underscore.lodash.U;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mrnavastar.invsync.Invsync;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;

import static com.mrnavastar.invsync.Invsync.log;

public class ConfigManager {

    public static File configFile;

    public static String SQL_User, SQL_Password, SQL_Server_Address, SQL_Server_Port, SQL_Database_Name, SQL_Database_Table_Name;
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
        SQLConfigProperties.addProperty("_comment_", "Settings for your mySQL server. This will be the same for all your servers.");
        SQLConfigProperties.addProperty("SQL_User", "admin");
        SQLConfigProperties.addProperty("SQL_Password", "1234");
        SQLConfigProperties.addProperty("SQL_Server_Address", "0.0.0.0");
        SQLConfigProperties.addProperty("SQL_Server_Port", "3306");
        SQLConfigProperties.addProperty("SQL_Database_Name", "database");
        SQLConfigProperties.addProperty("SQL_Database_Table_Name", "playerData"); //This is optional
        SQLConfigProperties.addProperty("SQL_Auto_Reconnect", true);

        JsonObject SQLProbablyShouldntTouch = new JsonObject();
        SQLProbablyShouldntTouch.addProperty("_comment_", "You probably shouldn't touch these unless you know what your doing.");
        SQLProbablyShouldntTouch.addProperty("SQL_Verify_Server_Certificate", false);
        SQLProbablyShouldntTouch.addProperty("SQL_Use_SSL", true);
        SQLProbablyShouldntTouch.addProperty("SQL_Require_SSL", true);

        JsonObject SyncConfigProperties = new JsonObject();
        SyncConfigProperties.addProperty("_comment_", "Settings for what to sync between servers. THIS MUST BE THE SAME BETWEEN SERVERS.");
        SyncConfigProperties.addProperty("Sync_Inv", true);
        SyncConfigProperties.addProperty("Sync_Armour", true);
        SyncConfigProperties.addProperty("Sync_eChest", true);
        SyncConfigProperties.addProperty("Sync_Xp", true);
        SyncConfigProperties.addProperty("Sync_Score", true);
        SyncConfigProperties.addProperty("Sync_Health", true);
        SyncConfigProperties.addProperty("Sync_Food_Level", true);

        JsonArray invSyncProperties = new JsonArray();
        invSyncProperties.add(SQLConfigProperties);
        invSyncProperties.add(SQLProbablyShouldntTouch);
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
=======
package com.mrnavastar.invsync.util;

import com.github.underscore.lodash.U;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mrnavastar.invsync.Invsync;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;

import static com.mrnavastar.invsync.Invsync.log;

public class ConfigManager {

    public static File configFile;

    public static String SQL_User, SQL_Password, SQL_Server_Address, SQL_Server_Port, SQL_Database_Name, SQL_Database_Table_Name;
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
        SQLConfigProperties.addProperty("_comment_", "Settings for your mySQL server. This will be the same for all your servers.");
        SQLConfigProperties.addProperty("SQL_User", "admin");
        SQLConfigProperties.addProperty("SQL_Password", "1234");
        SQLConfigProperties.addProperty("SQL_Server_Address", "0.0.0.0");
        SQLConfigProperties.addProperty("SQL_Server_Port", "3306");
        SQLConfigProperties.addProperty("SQL_Database_Name", "database");
        SQLConfigProperties.addProperty("SQL_Database_Table_Name", "playerData"); //This is optional
        SQLConfigProperties.addProperty("SQL_Auto_Reconnect", true);

        JsonObject SQLProbablyShouldntTouch = new JsonObject();
        SQLProbablyShouldntTouch.addProperty("_comment_", "You probably shouldn't touch these unless you know what your doing.");
        SQLProbablyShouldntTouch.addProperty("SQL_Verify_Server_Certificate", false);
        SQLProbablyShouldntTouch.addProperty("SQL_Use_SSL", true);
        SQLProbablyShouldntTouch.addProperty("SQL_Require_SSL", true);

        JsonObject SyncConfigProperties = new JsonObject();
        SyncConfigProperties.addProperty("_comment_", "Settings for what to sync between servers. THIS MUST BE THE SAME BETWEEN SERVERS.");
        SyncConfigProperties.addProperty("Sync_Inv", true);
        SyncConfigProperties.addProperty("Sync_Armour", true);
        SyncConfigProperties.addProperty("Sync_eChest", true);
        SyncConfigProperties.addProperty("Sync_Xp", true);
        SyncConfigProperties.addProperty("Sync_Score", true);
        SyncConfigProperties.addProperty("Sync_Health", true);
        SyncConfigProperties.addProperty("Sync_Food_Level", true);

        JsonArray invSyncProperties = new JsonArray();
        invSyncProperties.add(SQLConfigProperties);
        invSyncProperties.add(SQLProbablyShouldntTouch);
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
>>>>>>> 07023deeb41fdb819ebcd94f4511a21104422eee
