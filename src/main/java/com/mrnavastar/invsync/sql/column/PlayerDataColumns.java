package com.mrnavastar.invsync.sql.column;

import com.mrnavastar.invsync.util.ConfigManager;

import java.util.ArrayList;

public class PlayerDataColumns {

    public static ArrayList<Column> getColumns() {
        ArrayList<Column> columns = new ArrayList<>();

        if (ConfigManager.Sync_Inv) {
            for (int i = 0; i < 36; i++) {
                columns.add(new Column("inv" + i, "TEXT"));
            }
            columns.add(new Column("offHand", "TEXT"));
            columns.add(new Column("selectedSlot", "INTEGER"));
        }

        if (ConfigManager.Sync_Armour) {
            for (int i = 0; i < 4; i++) {
                columns.add(new Column("armour" + i, "TEXT"));
            }
        }

        if (ConfigManager.Sync_eChest) {
            for (int i = 0; i < 27; i++) {
                columns.add(new Column("eChest" + i, "TEXT"));
            }
        }

        if (ConfigManager.Sync_Xp) {
            columns.add(new Column("xp", "INTEGER"));
            columns.add(new Column("xpProgress", "REAL"));
        }

        if (ConfigManager.Sync_Score) columns.add(new Column("score", "INTEGER"));

        if (ConfigManager.Sync_Health) columns.add(new Column("health", "REAL"));

        if (ConfigManager.Sync_Food_Level) columns.add(new Column("foodLevel", "TEXT"));

        if (ConfigManager.Sync_Status_Effects) columns.add(new Column("statusEffects", "TEXT"));

        return columns;
    }
}
