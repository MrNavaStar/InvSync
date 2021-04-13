package com.mrnavastar.invsync.setup;

import com.mrnavastar.invsync.api.event.PlayerJoinCallback;
import com.mrnavastar.invsync.api.event.PlayerLeaveCallback;
import com.mrnavastar.invsync.conversion.PlayerRolesConversion;
import com.mrnavastar.invsync.sql.Table;
import com.mrnavastar.invsync.sql.column.PlayerRolesColumns;
import com.mrnavastar.invsync.util.ConfigManager;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.WorldSavePath;

import java.io.File;

public class PlayerRoles {

    public static Table playerRolesTable;
    public static String tableName;
    public static File playerRolesFile;

    public static void getConfigData() {
        tableName = ConfigManager.Player_Roles_Table_Name;
    }

    public static void subToEvents() {
        PlayerJoinCallback.EVENT.register((player, server) -> {
            PlayerRolesConversion.sqlToFile(playerRolesFile);
            return ActionResult.PASS;
        });

        PlayerLeaveCallback.EVENT.register((player, server) -> {
            PlayerRolesConversion.fileToSql(playerRolesFile);
            return ActionResult.PASS;
        });
    }

    public static void fileSetup() {
        ServerLifecycleEvents.SERVER_STARTING.register(server -> playerRolesFile = server.getSavePath(WorldSavePath.PLAYERDATA).resolve("player_roles").toFile());
    }

    public static void setupProcesses() {
        getConfigData();
        playerRolesTable = new Table(tableName, PlayerRolesColumns.getColumns());
        subToEvents();
        fileSetup();
    }
}
