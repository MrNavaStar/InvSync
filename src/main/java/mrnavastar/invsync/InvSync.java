package mrnavastar.invsync;

import com.google.common.io.Resources;
import com.google.gson.Gson;
import mc.microconfig.MicroConfig;
import mrnavastar.invsync.sync.SyncManager;
import mrnavastar.invsync.sync.mods.BaseSync;
import mrnavastar.sqlib.database.Database;
import mrnavastar.sqlib.database.MySQLDatabase;
import mrnavastar.sqlib.database.SQLiteDatabase;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.SharedConstants;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerAdvancementLoader;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

import java.io.*;
import java.util.ArrayList;

public class InvSync implements ModInitializer {

    public static final String MOD_ID = "InvSync";
    public static final FabricLoader FABRIC = FabricLoader.getInstance();
    public static Config config = MicroConfig.getOrCreate(MOD_ID, new Config());
    private static Database database;
    public static final ArrayList<String> playerDataBlacklist = new ArrayList<>();
    public static ServerAdvancementLoader advancementLoader;
    public static final Gson GSON = new Gson();

    @Override
    public void onInitialize() {
        log(Level.INFO, "Initializing...");

        if (config.DATABASE_TYPE.equals("SQLITE") && !config.SQLITE_DIRECTORY.equals("/path/to/folder")) {
            if (!new File(config.SQLITE_DIRECTORY).exists()) {
                log(Level.FATAL, "Halting initialization! " + config.SQLITE_DIRECTORY + " does not exist!");
                System.exit(0);
            }

            database = new SQLiteDatabase(MOD_ID, config.DATABASE_NAME, config.SQLITE_DIRECTORY);

        } else if (config.DATABASE_TYPE.equals("MYSQL") && !config.MYSQL_USERNAME.equals("username") && !config.MYSQL_PASSWORD.equals("password")) {
            database = new MySQLDatabase(MOD_ID, config.DATABASE_NAME, config.MYSQL_ADDRESS, config.MYSQL_PORT, config.MYSQL_USERNAME, config.MYSQL_PASSWORD);
        } else {
            log(Level.FATAL, "Halting initialization! You need to change some settings in the InvSync config");
            System.exit(0);
        }

        log(Level.INFO, "Database initialized successfully!");

        try {
            InputStream stream = Resources.getResource("assets/invsync/blacklists/" + SharedConstants.getGameVersion().getName()).openStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            playerDataBlacklist.addAll(reader.lines().toList());
            stream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            advancementLoader = server.getAdvancementLoader();
        });

        BaseSync.init(database);

        SyncManager.init();
        log(Level.INFO, "Complete!");
    }

    public static void log(Level level, String message) {
        LogManager.getLogger().log(level, "[" + MOD_ID + "] " + message);
    }
}