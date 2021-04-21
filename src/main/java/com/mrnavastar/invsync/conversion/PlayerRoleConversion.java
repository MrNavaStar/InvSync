package com.mrnavastar.invsync.conversion;

import dev.gegy.roles.PlayerRolesConfig;
import dev.gegy.roles.Role;
import dev.gegy.roles.api.RoleOwner;
import dev.gegy.roles.store.PlayerRoleSet;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.ListTag;

import java.util.ArrayList;
import java.util.Arrays;

import static com.mrnavastar.invsync.setup.PlayerRoles.playerRolesTable;

public class PlayerRoleConversion {

    public static void rolesToSql(PlayerEntity player) {
        playerRolesTable.startTransaction();
        String uuid = player.getUuid().toString();
        playerRolesTable.createRow("id", uuid);

        ListTag tag = ((RoleOwner) player).getRoles().serialize();
        playerRolesTable.saveString(uuid, "roles", tag.asString());

        playerRolesTable.endTransaction();
    }

    public static void sqlToRoles(PlayerEntity player) {
        playerRolesTable.startTransaction();
        String uuid = player.getUuid().toString();

        PlayerRoleSet roleSet = ((RoleOwner) player).getRoles();
        ListTag currentRoles = roleSet.serialize();

        ArrayList<String> currentRolesArray = new ArrayList<>(Arrays.asList(currentRoles.asString()
                .replaceAll("[^a-zA-Z1-9,]", "").split(",")));

        ArrayList<String> storedRoles = new ArrayList<>(Arrays.asList(playerRolesTable.loadString(uuid, "roles", "[]")
                .replaceAll("[^a-zA-Z1-9,]", "").split(",")));

        if (!storedRoles.toString().equals("[]")) {
            for (String role : currentRolesArray) {
                Role roleInstance = PlayerRolesConfig.get().get(role);
                if (roleInstance != null && !storedRoles.contains(role)) {
                    roleSet.remove(roleInstance);
                }
            }

            for (String role : storedRoles) {
                Role roleInstance = PlayerRolesConfig.get().get(role);
                if (roleInstance != null && !currentRolesArray.contains(role)) {
                    roleSet.add(roleInstance);
                }
            }
        }

        playerRolesTable.endTransaction();
    }
}
