package mrnavastar.invsync;

import mc.microconfig.MicroConfig;
import mrnavastar.invsync.sync.SyncManager;
import mrnavastar.invsync.sync.mods.BaseSync;
import mrnavastar.invsync.util.Config;
import mrnavastar.sqlib.database.Database;
import mrnavastar.sqlib.database.MySQLDatabase;
import mrnavastar.sqlib.database.SQLiteDatabase;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.ServerAdvancementLoader;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

import java.io.File;

public class InvSync implements ModInitializer {

    public static final String MOD_ID = "InvSync";
    public static Config config = MicroConfig.getOrCreate(MOD_ID, new Config());
    public static ServerAdvancementLoader advancementLoader;
    private Database database;

    @Override
    public void onInitialize() {
        log(Level.INFO, "Initializing...");

        switch (config.DATABASE_TYPE) {
            case "SQLITE" -> {
                if (!new File(config.SQLITE_DIRECTORY).exists()) {
                    log(Level.FATAL, "Halting initialization! " + config.SQLITE_DIRECTORY + " does not exist!");
                    System.exit(0);
                }
                database = new SQLiteDatabase(MOD_ID, config.DATABASE_NAME, config.SQLITE_DIRECTORY);
            }

            case "MYSQL" -> {
                if (config.MYSQL_USERNAME.equals("username") && config.MYSQL_PASSWORD.equals("password")) {
                    log(Level.FATAL, "Halting initialization! You need to change some settings in the InvSync config");
                    System.exit(0);
                }
                database = new MySQLDatabase(MOD_ID, config.DATABASE_NAME, config.MYSQL_ADDRESS, config.MYSQL_PORT, config.MYSQL_USERNAME, config.MYSQL_PASSWORD);
            }
        }

        log(Level.INFO, "Database initialized successfully!");

        ServerLifecycleEvents.SERVER_STARTING.register(server -> advancementLoader = server.getAdvancementLoader());
        BaseSync.init(database);
        SyncManager.init();

        log(Level.INFO, "Complete!");
    }

    public static void log(Level level, String message) {
        LogManager.getLogger().log(level, "[" + MOD_ID + "] " + message);
    }
}