package com.mrnavastar.invsync.setup;

import com.github.underscore.lodash.U;
import com.google.gson.*;
import com.mrnavastar.invsync.Invsync;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.Level;

import static com.mrnavastar.invsync.Invsync.log;

import java.io.*;

public class ConfigManager {

    public static File configFile;

    public static String Database_Name, Database_Directory, Player_Data_Table_Name, Player_Roles_Table_Name;
    public static boolean Enable_WAL_Mode, Sync_Inv, Sync_Armour, Sync_eChest, Sync_Xp, Sync_Score, Sync_Health, Sync_Food_Level, Sync_Status_Effects, Sync_Player_Roles;
    public static boolean started = false;

    public static void prepareConfigFile() {
        if (configFile != null) {
            return;
        }
        configFile = new File(FabricLoader.getInstance().getConfigDir().toString(), Invsync.MODID + "Config.json");
    }

    //Generate content for config file
    public static void createConfig() {
        JsonObject SQLConfigProperties = new JsonObject();
        SQLConfigProperties.addProperty("comment1", "Settings for your database. THESE MUST BE THE SAME BETWEEN SERVERS. (More info at https://github.com/MrNavaStar/invSync)");
        SQLConfigProperties.addProperty("Database_Name", "InvSync.db");
        SQLConfigProperties.addProperty("Player_Data_Table_Name", "PlayerData");
        SQLConfigProperties.addProperty("Database_Directory", "/Where/To/Create/Database");
        SQLConfigProperties.addProperty("Enable_WAL_Mode", false);

        SQLConfigProperties.addProperty("comment2", "Settings for what to sync between servers. THESE MUST BE THE SAME BETWEEN SERVERS.");
        SQLConfigProperties.addProperty("Sync_Inv", true);
        SQLConfigProperties.addProperty("Sync_Armour", true);
        SQLConfigProperties.addProperty("Sync_eChest", true);
        SQLConfigProperties.addProperty("Sync_Xp", true);
        SQLConfigProperties.addProperty("Sync_Score", true);
        SQLConfigProperties.addProperty("Sync_Health", true);
        SQLConfigProperties.addProperty("Sync_Food_Level", true);
        SQLConfigProperties.addProperty("Sync_Status_Effects", true);

        SQLConfigProperties.addProperty("comment3", "If you have the following mods installed, you can sync them too! THESE MUST BE THE SAME BETWEEN SERVERS.");
        SQLConfigProperties.addProperty("Player_Roles_Table_Name", "PlayerRoles");
        SQLConfigProperties.addProperty("Sync_Player_Roles", true);

        jsonWriter(SQLConfigProperties, configFile);
    }

    public static void jsonWriter(JsonObject input, File output) {
        try {
            FileWriter file = new FileWriter(output);
            file.write(U.formatJson(input.toString()));
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void jsonReader(File input) {
        JsonParser parser = new JsonParser();
        try {
            JsonElement jsonElement = parser.parse(new FileReader(input));
            JsonObject jsonObject = jsonElement.getAsJsonObject();

            Database_Name = jsonObject.get("Database_Name").getAsString();
            Player_Data_Table_Name = jsonObject.get("Player_Data_Table_Name").getAsString();
            Database_Directory = jsonObject.get("Database_Directory").getAsString();
            Enable_WAL_Mode = jsonObject.get("Enable_WAL_Mode").getAsBoolean();

            Sync_Inv = jsonObject.get("Sync_Inv").getAsBoolean();
            Sync_Armour = jsonObject.get("Sync_Armour").getAsBoolean();
            Sync_eChest = jsonObject.get("Sync_eChest").getAsBoolean();
            Sync_Xp = jsonObject.get("Sync_Xp").getAsBoolean();
            Sync_Score = jsonObject.get("Sync_Score").getAsBoolean();
            Sync_Health = jsonObject.get("Sync_Health").getAsBoolean();
            Sync_Food_Level = jsonObject.get("Sync_Food_Level").getAsBoolean();
            Sync_Status_Effects = jsonObject.get("Sync_Status_Effects").getAsBoolean();

            Player_Roles_Table_Name = jsonObject.get("Player_Roles_Table_Name").getAsString();
            Sync_Player_Roles = jsonObject.get("Sync_Player_Roles").getAsBoolean();

            started = true;

        } catch (FileNotFoundException ignore) {
        } catch (NullPointerException ignore) {
            log(Level.ERROR, "Whoops!, it looks like there is something wrong with your config");
            log(Level.INFO, "If you just updated the mod, the config format likely changed");
            log(Level.INFO, "Just delete the old config and let it regenerate");
            log(Level.INFO, "If this is not the case, you may have messed up the config format");
            log(Level.INFO, "Generate a new config or fix the format error");
        }
    }

    public static void loadConfig() {
        prepareConfigFile();
        if (!configFile.exists()) createConfig();
        jsonReader(configFile);
    }
}

