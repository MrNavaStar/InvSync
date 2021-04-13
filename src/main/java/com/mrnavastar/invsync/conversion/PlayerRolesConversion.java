package com.mrnavastar.invsync.conversion;

import com.mrnavastar.invsync.setup.PlayerRoles;
import com.mrnavastar.invsync.sql.SQLHandler;
import com.mrnavastar.invsync.util.ConfigManager;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class PlayerRolesConversion {

    public static void fileToSql(File file) {
        SQLHandler.connect();
        PlayerRoles.playerRolesTable.createRow("id", "file");
        if (ConfigManager.Sync_Player_Roles) {
            PlayerRoles.playerRolesTable.saveFile("file", "database", file);
        }
        SQLHandler.disconnect();
    }

    public static void sqlToFile(File file) {
        SQLHandler.connect();
        if (ConfigManager.Sync_Player_Roles) {
            file.delete();
            byte[] bytes = PlayerRoles.playerRolesTable.loadFile("file", "database");
            try {
                FileUtils.writeByteArrayToFile(file, bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        SQLHandler.disconnect();
    }
}
