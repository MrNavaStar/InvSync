package com.mrnavastar.invsync;

import com.mrnavastar.invsync.setup.ConfigManager;
import com.mrnavastar.invsync.setup.PlayerDataSetup;
import com.mrnavastar.invsync.sql.SQLHandler;
import net.fabricmc.api.ModInitializer;
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
            SQLHandler.connect();
        }

        if (SQLHandler.connection != null) {
            log(Level.INFO,"Successfully connected to database!");
            SQLHandler.disconnect();

            PlayerDataSetup.start();
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