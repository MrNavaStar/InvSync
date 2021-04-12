package com.mrnavastar.invsync.addons;

import com.mrnavastar.invsync.column.PlayerRolesColumns;
import com.mrnavastar.invsync.util.FileWatcher;
import com.mrnavastar.invsync.util.SQLHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static java.nio.file.StandardWatchEventKinds.*;

public class PlayerRoles {

    private static final String tableName = "PlayerRoles";

    private static Path playerRolesFilePath;
    private static File playerRolesFile;

    public static void getPlayerRolesFile(MinecraftServer server) {
        playerRolesFile = new File(server.getSavePath(WorldSavePath.PLAYERDATA).resolve("player_roles").toString().substring(2));
        playerRolesFilePath = Paths.get(playerRolesFile.getAbsolutePath());
    }

    public static void updateFileFromDatabase(File file) {
        byte[] bytes = SQLHandler.loadFile(tableName,"file", "database");
        file.delete();
        try {
            FileUtils.writeByteArrayToFile(file, bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveFileToDatabase(File file) {
        SQLHandler.saveFile(tableName, "file", "database", file);
    }

    public static void startWatchService(File file) {
        TimerTask task = new FileWatcher(file) {
            protected void onChange( File file ) {
                // here we code the action on a change
                System.out.println( "File " + file.getName() + " have change !" );
            }
        };

        Timer timer = new Timer();
        // repeat the check every second
        timer.schedule( task , new Date(), 1000 );
    }

    public static void start(MinecraftServer server) {
        SQLHandler.connect();
        SQLHandler.createTable(tableName, "type", "TEXT");
        SQLHandler.createRow("type", "file", tableName);
        PlayerRolesColumns.manageColumns();
        getPlayerRolesFile(server);
        startWatchService(playerRolesFile);
        SQLHandler.disconnect();
    }
}
