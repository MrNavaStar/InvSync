package com.mrnavastar.invsync.setup;

import com.mrnavastar.invsync.api.event.PlayerJoinCallback;

import com.mrnavastar.invsync.api.event.PlayerLeaveCallback;
import com.mrnavastar.invsync.conversion.PlayerRoleConversion;
import com.mrnavastar.invsync.sql.Table;
import com.mrnavastar.invsync.sql.column.PlayerRolesColumns;
import com.mrnavastar.invsync.util.ConfigManager;
import net.minecraft.util.ActionResult;

public class PlayerRoles {

    public static Table playerRolesTable;
    public static String tableName;

    public static void getConfigData() {
        tableName = ConfigManager.Player_Roles_Table_Name;
    }

    public static void subToEvents() {
        PlayerJoinCallback.EVENT.register((player, server) -> {

            return ActionResult.PASS;
        });

        PlayerLeaveCallback.EVENT.register((player, server) -> {
            PlayerRoleConversion.rolesToSql(player);
            return ActionResult.PASS;
        });
    }

    public static void setupProcesses() {
        getConfigData();
        playerRolesTable = new Table(tableName, PlayerRolesColumns.getColumns());
        subToEvents();
    }
}
