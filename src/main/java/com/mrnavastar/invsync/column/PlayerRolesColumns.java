package com.mrnavastar.invsync.column;

import java.util.ArrayList;

import static com.mrnavastar.invsync.util.SQLHandler.columnExists;
import static com.mrnavastar.invsync.util.SQLHandler.executeStatement;

public class PlayerRolesColumns {

    private static String tableName = "PlayerRoles";

    public static void manageColumns() {
        ArrayList<Column> columnsAdd = new ArrayList<>();

        if (!columnExists(tableName, "database")) columnsAdd.add(new Column("database", "BLOB"));
        if (!columnExists(tableName, "config")) columnsAdd.add(new Column( "config", "BLOB"));

        for (Column c : columnsAdd) {
            String sql = "ALTER TABLE " + tableName + " ADD " + c.getName() + " " + c.getType();
            executeStatement(sql);
        }
    }
}
