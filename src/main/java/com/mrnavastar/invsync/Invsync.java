package com.mrnavastar.invsync;

import com.mrnavastar.invsync.setup.PlayerDataSetup;
import com.mrnavastar.invsync.setup.PlayerRolesSetup;
import com.mrnavastar.invsync.sql.SQLHandler;
import com.mrnavastar.invsync.setup.ConfigManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Invsync implements ModInitializer {

    public static final String MODID = "invsync";
    //public static final String databaseName = ConfigManager.Database_Name;

    @Override
    public void onInitialize() {
        log(Level.INFO, "Initializing");

        ConfigManager.loadConfig();
        if (ConfigManager.started && !ConfigManager.Database_Directory.equals("/Where/To/Create/Database")) {
            SQLHandler.connect();
        }

        if (SQLHandler.connection != null) {
            log(Level.INFO,"Successfully connected to database!");
            SQLHandler.disconnect();

            PlayerDataSetup.start();

            //Enable syncing for supported mods when present
            for (ModContainer modContainer : FabricLoader.getInstance().getAllMods()) {
                if (modContainer.getMetadata().getId().equals("player_roles")) {
                    log(Level.INFO, "Player Roles found! Enabling support");
                    PlayerRolesSetup.start();
                } else {
                    SQLHandler.dropTable(ConfigManager.Player_Roles_Table_Name);
                }
            }
        }
    }

    //Stuff for console logging
    public static final Logger LOGGER = LogManager.getLogger();

    public static void log(Level level, String message) {
        log(level, message, (Object) null);
    }

    public static void log(Level level, String message, Object ... fields){
        LOGGER.log(level, "[" + MODID + "] " + message, fields);
    }
}