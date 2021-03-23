package com.mrnavastar.invsync;

import com.mrnavastar.invsync.api.event.PlayerJoinCallBack;
import com.mrnavastar.invsync.util.SQLHandler;
import com.mrnavastar.invsync.api.event.PlayerLeaveCallBack;
import com.mrnavastar.invsync.util.ConfigManager;
import com.mrnavastar.invsync.util.NBTtoSQL;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.ActionResult;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Invsync implements ModInitializer {

    public static final String MODID = "invsync";

    @Override
    public void onInitialize() {
        log(Level.INFO, "Initializing");

        ConfigManager.loadConfig();
        //SQLHandler.start();


        if (SQLHandler.connection != null) {

            //Copy data from sql to player data when player joins server
            PlayerJoinCallBack.EVENT.register((player, server) -> {
                log(Level.INFO, "Getting Player Data From SQL Server");
                return ActionResult.PASS;
            });

            //Copy Data from player data to sql when player leaves server
            PlayerLeaveCallBack.EVENT.register((player, server) -> {
                log(Level.INFO, "Saving Player Data to SQL Server");
                NBTtoSQL.convert(player);
                return ActionResult.PASS;
            });

            //Close connection to SQL database when server stops
            ServerLifecycleEvents.SERVER_STOPPING.register((server) -> SQLHandler.disconnectFromSQL());

            if (FabricLoader.getInstance().isModLoaded(MODID))
                log(Level.INFO, "Done");
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

