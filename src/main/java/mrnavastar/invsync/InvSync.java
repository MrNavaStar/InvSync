package mrnavastar.invsync;

import mc.microconfig.MicroConfig;
import mrnavastar.invsync.util.Converter;
import mrnavastar.invsync.util.Settings;
import mrnavastar.sqlib.api.Table;
import mrnavastar.sqlib.api.databases.Database;
import mrnavastar.sqlib.api.databases.MySQLDatabase;
import mrnavastar.sqlib.api.databases.SQLiteDatabase;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

import java.util.concurrent.TimeUnit;

public class InvSync implements ModInitializer {

    public static final String MODID = "InvSync";
    public static Table playerData;
    public static Settings settings;
    private static Database database;

    @Override
    public void onInitialize() {
        log(Level.INFO, "Initializing...");

        boolean validConfig = false;
        settings = MicroConfig.getOrCreate(MODID, new Settings());

        if (settings.DATABASE_TYPE.equals("SQLITE") && !settings.SQLITE_DIRECTORY.equals("/path/to/folder")) {
            database = new SQLiteDatabase(settings.DATABASE_NAME, settings.SQLITE_DIRECTORY);
            validConfig = true;
        }

        if (settings.DATABASE_TYPE.equals("MYSQL") && !settings.MYSQL_USERNAME.equals("username") && !settings.MYSQL_PASSWORD.equals("password")) {
            database = new MySQLDatabase(settings.DATABASE_NAME, settings.MYSQL_ADDRESS, settings.MYSQL_PORT, settings.MYSQL_USERNAME, settings.MYSQL_PASSWORD);
            validConfig = true;
        }

        if (validConfig) {
            playerData = database.createTable("PlayerData");

            ServerPlayConnectionEvents.JOIN.register((handler, s, server) -> {
                try {
                    TimeUnit.SECONDS.sleep(1); //Maybe we can find a less shit solution in the future
                    playerData.beginTransaction();
                    Converter.updatePlayerData(handler.getPlayer());
                    playerData.endTransaction();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });

            ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
                playerData.beginTransaction();
                Converter.savePlayerData(handler.getPlayer());
                playerData.endTransaction();
            });

            log(Level.INFO, "Complete!");
        } else log(Level.INFO, "Halting initialization! You need to change some settings in the InvSync config");
    }

    public static void log(Level level, String message){
        LogManager.getLogger().log(level, "[" + MODID + "] " + message);
    }
}