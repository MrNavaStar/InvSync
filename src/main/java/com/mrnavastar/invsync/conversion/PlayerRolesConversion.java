package com.mrnavastar.invsync.conversion;

import com.mrnavastar.invsync.setup.PlayerRoles;
import com.mrnavastar.invsync.sql.SQLHandler;
import com.mrnavastar.invsync.util.ConfigManager;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class PlayerRolesConversion {

    public static void fileToSql(File file) {
        if (ConfigManager.Sync_Player_Roles) {
            SQLHandler.connect();
            PlayerRoles.playerRolesTable.createRow("id", "file");
            PlayerRoles.playerRolesTable.saveFile("file", "database", file);
            SQLHandler.disconnect();
        }
    }

    public static void sqlToFile(File file) {
        if (ConfigManager.Sync_Player_Roles) {
            SQLHandler.connect();
            String path = file.getPath();
            byte[] bytes = PlayerRoles.playerRolesTable.loadFile("file", "database");
            if (bytes != null) {
                try {
                    file.delete();
                    FileUtils.writeByteArrayToFile(new File(path), bytes);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            SQLHandler.disconnect();
        }
    }
}
