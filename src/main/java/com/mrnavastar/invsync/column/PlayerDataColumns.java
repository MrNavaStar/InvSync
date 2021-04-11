package com.mrnavastar.invsync.column;

import com.mrnavastar.invsync.util.ConfigManager;

import static com.mrnavastar.invsync.util.SQLHandler.columnExists;
import static com.mrnavastar.invsync.util.SQLHandler.executeStatement;

import java.util.ArrayList;

public class PlayerDataColumns {

    private static final String TableName = ConfigManager.Database_Table_Name;
    public static final ArrayList<String> columnsTotal = new ArrayList<>();

    public static void manageColumns() {
        ArrayList<Column> columnsAdd = new ArrayList<>();

        if (ConfigManager.Sync_Inv) {
            for (int i = 0; i < 36; i++) {
                if (!columnExists(TableName, "inv0")) columnsAdd.add(new Column("inv" + i, "TEXT"));
                columnsTotal.add("inv" + i);
            }
            if (!columnExists(TableName, "offHand")) columnsAdd.add(new Column("offHand", "TEXT"));
            if (!columnExists(TableName, "selectedSlot")) columnsAdd.add(new Column("selectedSlot", "INTEGER"));
        }

        if (ConfigManager.Sync_Armour) {
            for (int i = 0; i < 4; i++) {
                if (!columnExists(TableName, "armour0")) columnsAdd.add(new Column("armour" + i, "TEXT"));
                columnsTotal.add("armour" + i);
            }
        }

        if (ConfigManager.Sync_eChest) {
            for (int i = 0; i < 27; i++) {
                if (!columnExists(TableName, "eChest0")) columnsAdd.add(new Column("eChest" + i, "TEXT"));
                columnsTotal.add("eChest" + i);
            }
        }

        if (ConfigManager.Sync_Xp) {
            if (!columnExists(TableName, "xp")) {
                columnsAdd.add(new Column("xp", "INTEGER"));
                columnsAdd.add(new Column("xpProgress", "REAL"));
            }
            columnsTotal.add("xp");
            columnsTotal.add("xpProgress");
        }

        if (ConfigManager.Sync_Score) {
            if (!columnExists(TableName, "score")) columnsAdd.add(new Column("score", "INTEGER"));
            columnsTotal.add("score");
        }

        if (ConfigManager.Sync_Health) {
            if (!columnExists(TableName, "health")) columnsAdd.add(new Column("health", "REAL"));
            columnsTotal.add("health");
        }

        if (ConfigManager.Sync_Food_Level) {
            if (!columnExists(TableName, "foodLevel")) columnsAdd.add(new Column("foodLevel", "TEXT"));
            columnsTotal.add("foodLevel");
        }

        if (ConfigManager.Sync_Status_Effects) {
            if (!columnExists(TableName, "statusEffects")) columnsAdd.add(new Column("statusEffects", "TEXT"));
            columnsTotal.add("statusEffects");
        }

        for (Column c : columnsAdd) {
            String sql = "ALTER TABLE " + TableName + " ADD " + c.getName() + " " + c.getType();
            executeStatement(sql);
        }
    }
}
