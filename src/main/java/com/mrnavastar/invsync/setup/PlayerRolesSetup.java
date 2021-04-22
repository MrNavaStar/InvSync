package com.mrnavastar.invsync.setup;

import com.mrnavastar.invsync.api.event.PlayerJoinCallback;
import com.mrnavastar.invsync.api.event.PlayerLeaveCallback;
import com.mrnavastar.invsync.conversion.PlayerRoleConversion;
import com.mrnavastar.invsync.sql.Table;
import com.mrnavastar.invsync.sql.column.PlayerRolesColumns;
import net.minecraft.util.ActionResult;

public class PlayerRolesSetup {

    public static String tableName = ConfigManager.Player_Roles_Table_Name;
    public static Table playerRolesTable = new Table(tableName, PlayerRolesColumns.getColumns());

    public static void start() {
        if (ConfigManager.Sync_Player_Roles) {
            PlayerJoinCallback.EVENT.register((player, server) -> {
                PlayerRoleConversion.sqlToRoles(player);
                return ActionResult.PASS;
            });

            PlayerLeaveCallback.EVENT.register((player, server) -> {
                PlayerRoleConversion.rolesToSql(player);
                return ActionResult.PASS;
            });
        }
    }
}
