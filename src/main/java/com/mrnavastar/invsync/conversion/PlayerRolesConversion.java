package com.mrnavastar.invsync.conversion;

import com.mrnavastar.invsync.sql.SQLHandler;
import com.mrnavastar.invsync.util.ConfigManager;
import org.apache.commons.io.FileUtils;

import static com.mrnavastar.invsync.setup.PlayerRoles.playerRolesTable;

import java.io.File;
import java.io.IOException;

public class PlayerRolesConversion {

    public static void fileToSql(File file) {
        if (ConfigManager.Sync_Player_Roles) {
            playerRolesTable.startTransaction();
            playerRolesTable.createRow("id", "file");
            playerRolesTable.saveFile("file", "database", file);
            playerRolesTable.endTransaction();
        }
    }

    public static void sqlToFile(File file) {
        if (ConfigManager.Sync_Player_Roles) {
            playerRolesTable.startTransaction();
            String path = file.getPath();
            byte[] bytes = playerRolesTable.loadFile("file", "database");
            if (bytes != null) {
                try {
                    file.delete();
                    FileUtils.writeByteArrayToFile(new File(path), bytes);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            playerRolesTable.endTransaction();
        }
    }
}
