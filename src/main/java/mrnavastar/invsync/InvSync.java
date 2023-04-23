package mrnavastar.invsync;

import com.google.common.io.Resources;
import com.google.gson.Gson;
import mc.microconfig.MicroConfig;
import mrnavastar.invsync.api.ServerSyncEvents;
import mrnavastar.invsync.services.VanillaSync;
import mrnavastar.invsync.services.ModSync;
import mrnavastar.invsync.services.Settings;
import mrnavastar.sqlib.api.DataContainer;
import mrnavastar.sqlib.api.Table;
import mrnavastar.sqlib.api.databases.Database;
import mrnavastar.sqlib.api.databases.MySQLDatabase;
import mrnavastar.sqlib.api.databases.SQLiteDatabase;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.ServerAdvancementLoader;
import net.minecraft.server.network.ServerPlayerEntity;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class InvSync implements ModInitializer {

    public static final String MODID = "InvSync";
    public static Settings settings;
    private static Database database;
    public static Table playerData;
    public static final ArrayList<String> playerDataBlacklist = new ArrayList<>();
    public static ServerAdvancementLoader advancementLoader;
    public static final Gson GSON = new Gson();

    @Override
    public void onInitialize() {
        log(Level.INFO, "Initializing...");

        settings = MicroConfig.getOrCreate(MODID, new Settings());

        if (settings.DATABASE_TYPE.equals("SQLITE") && !settings.SQLITE_DIRECTORY.equals("/path/to/folder")) {
            if (!new File(settings.SQLITE_DIRECTORY).exists()) {
                log(Level.FATAL, "Halting initialization! " + settings.SQLITE_DIRECTORY + " does not exist!");
                System.exit(0);
            }

            database = new SQLiteDatabase(settings.DATABASE_NAME, settings.SQLITE_DIRECTORY);

        } else if (settings.DATABASE_TYPE.equals("MYSQL") && !settings.MYSQL_USERNAME.equals("username") && !settings.MYSQL_PASSWORD.equals("password")) {
            database = new MySQLDatabase(settings.DATABASE_NAME, settings.MYSQL_ADDRESS, settings.MYSQL_PORT, settings.MYSQL_USERNAME, settings.MYSQL_PASSWORD);
        } else {
            log(Level.FATAL, "Halting initialization! You need to change some settings in the InvSync config");
            System.exit(0);
        }

        playerData = database.createTable("PlayerData");
        log(Level.INFO, "Database initialized successfully!");

        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            try {
                String gameVersion = server.getVersion();
                InputStream stream = Resources.getResource("assets/invsync/blacklists/" + gameVersion).openStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                playerDataBlacklist.addAll(reader.lines().toList());
                stream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            advancementLoader = server.getAdvancementLoader();
        });

        ServerPlayConnectionEvents.JOIN.register(((handler, sender, server) -> {
            new Thread(() -> {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                ServerPlayerEntity player = handler.getPlayer();
                DataContainer playerDataContainer = playerData.get(player.getUuid());

                playerData.beginTransaction();
                ServerSyncEvents.FETCH_PLAYER_DATA.invoker().handle(player, playerDataContainer);
                playerData.endTransaction();
            }).start();
        }));

        ServerPlayConnectionEvents.DISCONNECT.register(((handler, server) -> {
            ServerPlayerEntity player = handler.getPlayer();
            DataContainer playerDataContainer = playerData.get(player.getUuid());
            if (playerDataContainer == null) playerDataContainer = playerData.createDataContainer(player.getUuid());

            playerData.beginTransaction();
            ServerSyncEvents.SAVE_PLAYER_DATA.invoker().handle(player, playerDataContainer);
            playerData.endTransaction();
        }));

        VanillaSync.init();
        if (FabricLoader.getInstance().isModLoaded("cobblemon")) ModSync.initCobblemon();

        log(Level.INFO, "Complete!");
    }

    public static void log(Level level, String message) {
        LogManager.getLogger().log(level, "[" + MODID + "] " + message);
    }
}
