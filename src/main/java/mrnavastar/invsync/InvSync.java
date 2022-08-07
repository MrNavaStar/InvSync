package mrnavastar.invsync;

import mc.microconfig.MicroConfig;
import mrnavastar.invsync.api.ServerSaveEvent;
import mrnavastar.invsync.api.ServerSyncEvents;
import mrnavastar.invsync.services.CoreSyncProcedures;
import mrnavastar.invsync.services.Settings;
import mrnavastar.sqlib.api.DataContainer;
import mrnavastar.sqlib.api.Table;
import mrnavastar.sqlib.api.databases.Database;
import mrnavastar.sqlib.api.databases.MySQLDatabase;
import mrnavastar.sqlib.api.databases.SQLiteDatabase;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.ServerAdvancementLoader;
import net.minecraft.server.network.ServerPlayerEntity;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class InvSync implements ModInitializer {

    public static final String MODID = "InvSync";
    public static Settings settings;
    private static Database database;
    public static ServerAdvancementLoader advancementLoader;

    @Override
    public void onInitialize() {
        log(Level.INFO, "Initializing...");

        boolean validConfig = false;
        settings = MicroConfig.getOrCreate(MODID, new Settings());

        if (settings.DATABASE_TYPE.equals("SQLITE") && !settings.SQLITE_DIRECTORY.equals("/path/to/folder")) {
            if (!new File(settings.SQLITE_DIRECTORY).exists()) {
                log(Level.FATAL, "Halting initialization! " + settings.SQLITE_DIRECTORY + " does not exist!");
                System.exit(0);
            }

            database = new SQLiteDatabase(settings.DATABASE_NAME, settings.SQLITE_DIRECTORY);
            validConfig = true;
        }

        if (settings.DATABASE_TYPE.equals("MYSQL") && !settings.MYSQL_USERNAME.equals("username") && !settings.MYSQL_PASSWORD.equals("password")) {
            database = new MySQLDatabase(settings.DATABASE_NAME, settings.MYSQL_ADDRESS, settings.MYSQL_PORT, settings.MYSQL_USERNAME, settings.MYSQL_PASSWORD);
            validConfig = true;
        }

        if (!validConfig) {
            log(Level.FATAL, "Halting initialization! You need to change some settings in the InvSync config");
            System.exit(0);
        }

        Table playerData = database.createTable(MODID + "-PlayerData");
        Table serverData = database.createTable(MODID + "-ServerData");
        log(Level.INFO, "Database initialized successfully!");

        ServerLifecycleEvents.SERVER_STARTING.register(server -> advancementLoader = server.getAdvancementLoader());

        ServerPlayConnectionEvents.JOIN.register(((handler, sender, server) -> {
            try {
                TimeUnit.SECONDS.sleep(1); //Maybe we can find a better solution in the future
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            ServerPlayerEntity player = handler.getPlayer();
            DataContainer playerDataContainer = playerData.get(player.getUuid());

            playerData.beginTransaction();
            ServerSyncEvents.FETCH_PLAYER_DATA.invoker().handle(player, playerDataContainer);
            playerData.endTransaction();
            serverData.beginTransaction();
            ServerSyncEvents.FETCH_SERVER_DATA.invoker().handle(server, serverData);
            serverData.endTransaction();
        }));

        ServerPlayConnectionEvents.DISCONNECT.register(((handler, server) -> {
            ServerPlayerEntity player = handler.getPlayer();
            DataContainer playerDataContainer = playerData.get(player.getUuid());
            if (playerDataContainer == null) {
                playerDataContainer = playerData.createDataContainer(player.getUuid());
                playerData.put(playerDataContainer);
            }

            playerData.beginTransaction();
            ServerSyncEvents.SAVE_PLAYER_DATA.invoker().handle(player, playerDataContainer);
            playerData.endTransaction();
            serverData.beginTransaction();
            ServerSyncEvents.SAVE_SERVER_DATA.invoker().handle(server, serverData);
            serverData.endTransaction();
        }));

        ServerSaveEvent.SAVE_EVERYTHING_EVENT.register(server -> {
            playerData.beginTransaction();
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                DataContainer playerDataContainer = playerData.get(player.getUuid());
                if (playerDataContainer == null) {
                    playerDataContainer = playerData.createDataContainer(player.getUuid());
                    playerData.put(playerDataContainer);
                }
                ServerSyncEvents.SAVE_PLAYER_DATA.invoker().handle(player, playerDataContainer);
            }
            playerData.endTransaction();
        });

        CoreSyncProcedures.init();
        log(Level.INFO, "Complete!");
    }

    public static void log(Level level, String message) {
        LogManager.getLogger().log(level, "[" + MODID + "] " + message);
    }
}
