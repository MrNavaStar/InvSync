package com.mrnavastar.invsync.addons;

import com.mrnavastar.invsync.column.PlayerRolesColumns;
import com.mrnavastar.invsync.util.SQLHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;

import java.io.File;

public class PlayerRoles {

    private static final String tableName = "PlayerRoles";

    private static File playerRolesFile;

    public static void getPlayerRolesFile(MinecraftServer server) {
        playerRolesFile = new File(server.getSavePath(WorldSavePath.PLAYERDATA).resolve("player_roles").toString().substring(2));
    }

    public static void start(MinecraftServer server) {
        SQLHandler.connect();
        SQLHandler.createTable(tableName, "type", "TEXT");
        SQLHandler.createRow("type", "file", tableName);
        PlayerRolesColumns.manageColumns();
        getPlayerRolesFile(server);
        SQLHandler.saveFile(tableName, "file", "database", new File(playerRolesFile.getAbsolutePath()));
        System.out.println(new File(playerRolesFile.getAbsolutePath()));
        System.out.println(playerRolesFile.getAbsolutePath());
        SQLHandler.disconnect();
    }
}
