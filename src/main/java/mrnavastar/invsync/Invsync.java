package mrnavastar.invsync;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import mrnavastar.invsync.util.Converter;
import mrnavastar.invsync.util.Settings;
import mrnavastar.sqlib.api.SqlTypes;
import mrnavastar.sqlib.api.Table;
import mrnavastar.sqlib.util.Database;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

import java.util.concurrent.TimeUnit;

public class Invsync implements ModInitializer {

    public static final String MODID = "InvSync";
    public static Table playerData;
    public static Settings settings;

    @Override
    public void onInitialize() {
        log(Level.INFO, "Initializing...");

        boolean validConfig = false;
        AutoConfig.register(Settings.class, JanksonConfigSerializer::new);
        settings = AutoConfig.getConfigHolder(Settings.class).getConfig();

        if (settings.DATABASE_TYPE.equals(SqlTypes.SQLITE)) {
            validConfig = !settings.SQLITE_DIRECTORY.equals("/path/to/folder");
        }

        if (settings.DATABASE_TYPE.equals(SqlTypes.MYSQL)) {
            validConfig = (!settings.MYSQL_USERNAME.equals("username") && !settings.MYSQL_PASSWORD.equals("password"));
        }

        if (validConfig) {
            Database.TYPE = settings.DATABASE_TYPE;
            Database.DATABASE_NAME = settings.DATABASE_NAME;

            Database.SQLITE_DIRECTORY = settings.SQLITE_DIRECTORY;

            Database.MYSQL_ADDRESS = settings.MYSQL_ADDRESS;
            Database.MYSQL_PORT = settings.MYSQL_PORT;
            Database.MYSQL_USERNAME = settings.MYSQL_USERNAME;
            Database.MYSQL_PASSWORD = settings.MYSQL_PASSWORD;

            Database.init();
            playerData = new Table(MODID + "PlayerData");

            ServerPlayConnectionEvents.INIT.register((handler, server) ->  {
                /*try {
                    TimeUnit.SECONDS.sleep(1); //Maybe we can find a less shit solution in the future
                    playerData.beginTransaction();
                    Converter.updatePlayerData(handler.getPlayer());
                    playerData.endTransaction();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
                playerData.beginTransaction();
                Converter.updatePlayerData(handler.getPlayer());
                playerData.endTransaction();
            });

            ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
                playerData.beginTransaction();
                Converter.savePlayerData(handler.getPlayer());
                playerData.endTransaction();
            });

            log(Level.INFO, "Complete!");
        }
        else log(Level.INFO, "Halting initialization! You need to change some settings in the InvSync config");
    }

    public static void log(Level level, String message){
        LogManager.getLogger().log(level, "[" + MODID + "] " + message);
    }
}