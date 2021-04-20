package com.mrnavastar.invsync;

import com.mrnavastar.invsync.setup.PlayerData;
import com.mrnavastar.invsync.setup.PlayerRoles;
import com.mrnavastar.invsync.sql.SQLHandler;
import com.mrnavastar.invsync.util.ConfigManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Invsync implements ModInitializer {

    public static final String MODID = "invsync";

    @Override
    public void onInitialize() {
        log(Level.INFO, "Initializing");

        ConfigManager.loadConfig();
        if (ConfigManager.started && !ConfigManager.Database_Directory.equals("/Where/To/Create/Database")) {
            SQLHandler.start();
        }

        if (SQLHandler.connection != null) {
            log(Level.INFO,"Successfully connected to database!");

            PlayerData.setupProcesses();

            /*
            //Enable syncing for supported mods when present
            for (ModContainer modContainer : FabricLoader.getInstance().getAllMods()) {
                if (modContainer.getMetadata().getId().equals("player_roles")) {
                    log(Level.INFO, "Player Roles found! Enabling support");
                    PlayerRoles.setupProcesses();
                }
            }
             */

            SQLHandler.disconnect();
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