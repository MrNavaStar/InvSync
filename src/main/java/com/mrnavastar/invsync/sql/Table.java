package com.mrnavastar.invsync.sql;

import com.mrnavastar.invsync.sql.column.Column;

import java.io.File;
import java.util.ArrayList;

public class Table {

    private final String tableName;

    public Table(String tableName, ArrayList<Column> columns) {
        this.tableName = tableName;
        SQLHandler.connect();
        SQLHandler.createTable(tableName);
        SQLHandler.createTable(tableName + "_new");

        for (Column c : columns) {
            if (!SQLHandler.columnExists(tableName, c.getName())) {
                String sql = "ALTER TABLE " + tableName + " ADD " + c.getName() + " " + c.getType();
                SQLHandler.executeStatement(sql);
            }
            String sql = "ALTER TABLE " + tableName + "_new ADD " + c.getName() + " " + c.getType();
            SQLHandler.executeStatement(sql);
        }

        String columnList = columns.toString().replace("[", "").replace("]", "");
        SQLHandler.executeStatement("PRAGMA cache_size=10000;");
        SQLHandler.executeStatement("PRAGMA foreign_keys=off;");
        SQLHandler.executeStatement("BEGIN TRANSACTION;");
        SQLHandler.executeStatement("PRAGMA cache_size=1000;");
        SQLHandler.executeStatement("SELECT " + columnList + " INTO " + tableName + "_new FROM " + tableName + ";");
        SQLHandler.executeStatement("DROP TABLE " + tableName + ";");
        SQLHandler.executeStatement("ALTER TABLE " + tableName + "_temp RENAME TO " + tableName + ";");
        SQLHandler.executeStatement("COMMIT;");
        SQLHandler.executeStatement("PRAGMA foreign_keys=on;");

        SQLHandler.disconnect();
    }

    public void startTransaction() {
        SQLHandler.connect();
        SQLHandler.executeStatement("PRAGMA cache_size=10000;");
        SQLHandler.executeStatement("BEGIN TRANSACTION;");
    }

    public void endTransaction() {
        SQLHandler.disconnect();
        SQLHandler.executeStatement("COMMIT;");
    }

    public void createRow(String id, String value) {
        SQLHandler.createRow(tableName, id, value);
    }

    public void saveString(String key, String column, String data) {
        SQLHandler.saveString(tableName, key, column, data);
    }

    public void saveInt(String key, String column, int data) {
        SQLHandler.saveInt(tableName, key, column, data);
    }

    public void saveFloat(String key, String column, float data) {
        SQLHandler.saveFloat(tableName, key, column, data);
    }

    public void saveFile(String key, String column, File file) {
        SQLHandler.saveFile(tableName, key, column, file);
    }

    public String loadString(String key, String column, String defaultValue) {
        return SQLHandler.loadString(tableName, key, column, defaultValue);
    }

    public int loadInt(String key, String column, int defaultValue) {
        return SQLHandler.loadInt(tableName, key, column, defaultValue);
    }

    public float loadFloat(String key, String column, float defaultValue) {
        return SQLHandler.loadFloat(tableName, key, column, defaultValue);
    }

    public byte[] loadFile(String key, String column) {
        return SQLHandler.loadFile(tableName, key, column);
    }
}
