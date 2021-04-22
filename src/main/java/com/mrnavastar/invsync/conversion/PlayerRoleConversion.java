package com.mrnavastar.invsync.conversion;

import dev.gegy.roles.*;
import dev.gegy.roles.api.RoleOwner;
import dev.gegy.roles.store.PlayerRoleSet;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.ListTag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import static com.mrnavastar.invsync.setup.PlayerRolesSetup.playerRolesTable;

public class PlayerRoleConversion {

    public static void rolesToSql(PlayerEntity player) {
        playerRolesTable.startTransaction();
        String uuid = player.getUuid().toString();
        playerRolesTable.createRow("id", uuid);

        ListTag currentRoles = ((RoleOwner) player).getRoles().serialize();
        ArrayList<String> roles = new ArrayList<>();

        currentRoles.iterator().forEachRemaining(role -> roles.add(Objects.requireNonNull(PlayerRolesConfig.get().get(role.asString().replace("\"", ""))).getName()));
        playerRolesTable.set(uuid, "roles", roles.toString());

        playerRolesTable.endTransaction();
    }

    public static void sqlToRoles(PlayerEntity player) {
        playerRolesTable.startTransaction();
        String uuid = player.getUuid().toString();

        PlayerRoleSet roleSet = ((RoleOwner) player).getRoles();
        ListTag currentRoles = roleSet.serialize();

        ArrayList<Role> currentRolesArray = new ArrayList<>();
        ArrayList<Role> storedRoles = new ArrayList<>();

        String[] storedRoleNames = playerRolesTable.get(uuid, "roles", "[]")
                .replace("[", "").replace("]", "").split(", ");

        currentRoles.iterator().forEachRemaining(role -> currentRolesArray.add(PlayerRolesConfig.get().get(role.asString().replace("\"", ""))));

        for (String role : storedRoleNames) {
            storedRoles.add(PlayerRolesConfig.get().get(role));
        }

        for (Role role : currentRolesArray) {
            if (!storedRoles.contains(role)) {
                roleSet.remove(role);
            }
        }

        if (!Arrays.toString(storedRoleNames).equals("[]")) {
            for (Role role : storedRoles) {
                if (!currentRolesArray.contains(role)) {
                    roleSet.add(role);
                }
            }
        }

        playerRolesTable.endTransaction();
    }
}
