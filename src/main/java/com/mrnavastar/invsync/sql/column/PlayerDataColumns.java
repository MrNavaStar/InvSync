package com.mrnavastar.invsync.sql.column;

import com.mrnavastar.invsync.setup.ConfigManager;

import java.util.ArrayList;

public class PlayerDataColumns {

    private static final ArrayList<Column> columns = new ArrayList<>();

    public static ArrayList<Column> getColumns() {


        if (ConfigManager.Sync_Inv) {
            for (int i = 0; i < 36; i++) {
                addColumn("inv" + i, "TEXT");
            }
            addColumn("offHand", "TEXT");
            addColumn("selectedSlot", "INTEGER");
        }

        if (ConfigManager.Sync_Armour) {
            for (int i = 0; i < 4; i++) {
                addColumn("armour" + i, "TEXT");
            }
        }
        if (ConfigManager.Sync_eChest) {
            for (int i = 0; i < 27; i++) {
                addColumn("eChest" + i, "TEXT");
            }
        }

        if (ConfigManager.Sync_Xp) {
            addColumn("xp", "INTEGER");
            addColumn("xpProgress", "REAL");
        }

        if (ConfigManager.Sync_Score)  addColumn("score", "INTEGER");

        if (ConfigManager.Sync_Health)  addColumn("health", "REAL");

        if (ConfigManager.Sync_Food_Level)  addColumn("foodLevel", "TEXT");

        if (ConfigManager.Sync_Status_Effects)  addColumn("statusEffects", "TEXT");

        return columns;
    }

    public static void addColumn(String name, String type) {
        columns.add(new Column(name, type));
    }
}
