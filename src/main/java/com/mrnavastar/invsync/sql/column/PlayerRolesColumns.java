package com.mrnavastar.invsync.sql.column;

import com.mrnavastar.invsync.setup.ConfigManager;

import java.util.ArrayList;

public class PlayerRolesColumns {

    public static ArrayList<Column> getColumns() {
        ArrayList<Column> columns = new ArrayList<>();

        if (ConfigManager.Sync_Player_Roles) columns.add(new Column("roles", "TEXT"));

        return columns;
    }
}
