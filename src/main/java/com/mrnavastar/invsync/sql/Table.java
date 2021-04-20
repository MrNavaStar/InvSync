package com.mrnavastar.invsync.sql;

import com.mrnavastar.invsync.sql.column.Column;

import java.io.File;
import java.util.ArrayList;

public class Table {

    private final String tableName;

    public Table(String tableName, ArrayList<Column> columns) {
        this.tableName = tableName;

        startTransaction();
        SQLHandler.createTable(tableName);
        SQLHandler.createTable(tableName + "_new");

        StringBuilder builder = new StringBuilder();

        for (Column c : columns) {
            String columnName = c.getName();
            String columnType = c.getType();

            if (!SQLHandler.columnExists(tableName, c.getName())) {
                SQLHandler.executeStatement("ALTER TABLE " + tableName + " ADD " + columnName + " " + columnType);
            }
            SQLHandler.executeStatement("ALTER TABLE " + tableName + "_new ADD " + columnName + " " + columnType);

            builder.append(columnName).append(", ");
        }
        String columnList = builder.substring(0, builder.length() - 2);

        SQLHandler.executeStatement("INSERT INTO " + tableName + "_new (id, " + columnList + ") SELECT id, " + columnList + " FROM " + tableName + ";");
        endTransaction();

        startTransaction();
        SQLHandler.executeStatement("DROP TABLE " + tableName + ";");
        SQLHandler.executeStatement("ALTER TABLE " + tableName + "_new RENAME TO " + tableName + ";");
        endTransaction();
    }

    public void startTransaction() {
        SQLHandler.connect();
        SQLHandler.executeStatement("PRAGMA cache_size=10000;");
        SQLHandler.executeStatement("BEGIN TRANSACTION;");
    }

    public void endTransaction() {
        SQLHandler.executeStatement("COMMIT;");
        SQLHandler.disconnect();
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