package com.mrnavastar.invsync.addons;

import com.mrnavastar.invsync.column.PlayerRolesColumns;
import com.mrnavastar.invsync.util.SQLHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class PlayerRoles {

    private static final String tableName = "PlayerRoles";

    private static File playerRolesFile;
    private static String path;

    public static void getPlayerRolesFile(MinecraftServer server) {
        playerRolesFile = new File(server.getSavePath(WorldSavePath.PLAYERDATA).resolve("player_roles").toString().substring(2));
        path = playerRolesFile.getAbsolutePath();
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

    public static void start(MinecraftServer server) {
        SQLHandler.connect();
        SQLHandler.createTable(tableName, "type", "TEXT");
        SQLHandler.createRow("type", "file", tableName);
        PlayerRolesColumns.manageColumns();
        getPlayerRolesFile(server);
        SQLHandler.disconnect();
    }
}
