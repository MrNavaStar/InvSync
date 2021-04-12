package com.mrnavastar.invsync;

import com.mrnavastar.invsync.addons.PlayerRoles;
import com.mrnavastar.invsync.api.event.PlayerJoinCallback;
import com.mrnavastar.invsync.util.SQLHandler;
import com.mrnavastar.invsync.api.event.PlayerLeaveCallback;
import com.mrnavastar.invsync.util.ConfigManager;
import com.mrnavastar.invsync.conversion.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.ModContainer;
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
        if (!ConfigManager.Database_Directory.equals("/Where/To/Create/Database")) {
            SQLHandler.start();
        }

        if (SQLHandler.connection != null) {
            log(Level.INFO,"Successfully connected to database!");

            //Copy data from sql to player data when player joins server
            PlayerJoinCallback.EVENT.register((player, server) -> {
                log(Level.INFO, "Getting Player Data From database");
                SQLtoNBT.convert(player);
                return ActionResult.PASS;
            });

            //Copy Data from player data to sql when player leaves server
            PlayerLeaveCallback.EVENT.register((player, server) -> {
                log(Level.INFO, "Saving Player Data to database");
                NBTtoSQL.convert(player);
                return ActionResult.PASS;
            });

            //Enable syncing for supported mods when present
            for (ModContainer modContainer : FabricLoader.getInstance().getAllMods()) {
                if (modContainer.getMetadata().getId().equals("player_roles")) {
                    log(Level.INFO, "Player Roles found! Enabling support");
                    ServerLifecycleEvents.SERVER_STARTING.register(PlayerRoles::start);
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