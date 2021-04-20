package com.mrnavastar.invsync.conversion;

import dev.gegy.roles.Role;
import dev.gegy.roles.api.RoleOwner;
import dev.gegy.roles.store.PlayerRoleSet;
import net.minecraft.entity.player.PlayerEntity;

import java.util.ArrayList;
import java.util.Arrays;

import static com.mrnavastar.invsync.setup.PlayerData.playerDataTable;
import static com.mrnavastar.invsync.setup.PlayerRoles.playerRolesTable;

public class PlayerRoleConversion {

    public static void rolesToSql(PlayerEntity player) {
        playerRolesTable.startTransaction();
        String uuid = player.getUuid().toString();
        playerDataTable.createRow("id", uuid);

        PlayerRoleSet roles = ((RoleOwner) player).getRoles();
        playerRolesTable.saveString(uuid, "roles", Arrays.toString(roles.stream().toArray()));

        playerRolesTable.endTransaction();
    }

    /*public static void sqlToRoles(PlayerEntity player) {
        playerRolesTable.startTransaction();
        String uuid = player.getUuid().toString();

        String[] roles = playerRolesTable.loadString(uuid, "roles", "[]").replace("[", "").replace("]", "").split(", ");
        ArrayList<Role> rolesParsed = new ArrayList<>();

        for (String role : roles) {
            String[] strArr = role.replaceAll("\"", "").replace("[", "").replace("]", "")
                    .replace("(", "").replace(")", "").split(" ");

            rolesParsed.add()
        }

        PlayerRoleSet currentRoles = ((RoleOwner) player).getRoles();

        if (currentRoles.hasRole())


        playerDataTable.endTransaction();
    }*/
}
