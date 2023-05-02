package mrnavastar.invsync;

import com.google.common.io.Resources;
import com.google.gson.Gson;
import mc.microconfig.MicroConfig;
import mrnavastar.invsync.services.ModSync;
import mrnavastar.invsync.services.Settings;
import mrnavastar.invsync.services.SyncManager;
import mrnavastar.sqlib.Table;
import mrnavastar.sqlib.database.Database;
import mrnavastar.sqlib.database.MySQLDatabase;
import mrnavastar.sqlib.database.SQLiteDatabase;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.ServerAdvancementLoader;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

import java.io.*;
import java.util.ArrayList;

public class InvSync implements ModInitializer {

    public static final String MOD_ID = "InvSync";
    private static final FabricLoader FABRIC = FabricLoader.getInstance();
    public static Settings settings = MicroConfig.getOrCreate(MOD_ID, new Settings());
    private static Database database;
    public static final ArrayList<String> playerDataBlacklist = new ArrayList<>();
    public static ServerAdvancementLoader advancementLoader;
    public static final Gson GSON = new Gson();

    @Override
    public void onInitialize() {
        log(Level.INFO, "Initializing...");

        if (settings.DATABASE_TYPE.equals("SQLITE") && !settings.SQLITE_DIRECTORY.equals("/path/to/folder")) {
            if (!new File(settings.SQLITE_DIRECTORY).exists()) {
                log(Level.FATAL, "Halting initialization! " + settings.SQLITE_DIRECTORY + " does not exist!");
                System.exit(0);
            }

            database = new SQLiteDatabase(MOD_ID, settings.DATABASE_NAME, settings.SQLITE_DIRECTORY);

        } else if (settings.DATABASE_TYPE.equals("MYSQL") && !settings.MYSQL_USERNAME.equals("username") && !settings.MYSQL_PASSWORD.equals("password")) {
            database = new MySQLDatabase(MOD_ID, settings.DATABASE_NAME, settings.MYSQL_ADDRESS, settings.MYSQL_PORT, settings.MYSQL_USERNAME, settings.MYSQL_PASSWORD);
        } else {
            log(Level.FATAL, "Halting initialization! You need to change some settings in the InvSync config");
            System.exit(0);
        }

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

        ModSync.initBase(database);
        if (FABRIC.isModLoaded("cobblemon")) ModSync.initCobblemon(database);

        SyncManager.init();
        log(Level.INFO, "Complete!");
    }

    public static void log(Level level, String message) {
        LogManager.getLogger().log(level, "[" + MOD_ID + "] " + message);
    }
}