package mrnavastar.invsync.services;

import mrnavastar.sqlib.DataContainer;
import mrnavastar.sqlib.Table;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class SyncManager {

    private static final HashMap<String, Table> registeredMods = new HashMap<>();

    public static void init() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> new Thread(() -> {
            ServerPlayerEntity player = handler.getPlayer();

            DataContainer baseData = registeredMods.get("base").get(player.getUuid());
            if (baseData == null) return;

            //Wait until data is free from all other servers
            while (baseData.getBool("dataInUse")) {
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            invokeLoad(player);
        }).start());

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> invokeSave(handler.getPlayer()));
    }

    public static void registerMod(String modId, Table table) {
        registeredMods.put(modId, table);
    }

    public static void invokeLoad(ServerPlayerEntity player) {
        registeredMods.forEach((modId, table) -> {
            DataContainer data = table.get(player.getUuid());
            if (data == null) return;

            table.beginTransaction();
            SyncEvents.LOAD_PLAYER_DATA.getInvoker(modId).forEach(modHandler -> modHandler.handle(player, data));
            table.endTransaction();
        });
    }

    public static void invokeSave(ServerPlayerEntity player) {
        registeredMods.forEach((modId, table) -> {
            DataContainer data = Objects.requireNonNullElse(table.get(player.getUuid()), table.createDataContainer(player.getUuid()));
            table.beginTransaction();
            SyncEvents.SAVE_PLAYER_DATA.getInvoker(modId).forEach(modHandler -> modHandler.handle(player, data));
            table.endTransaction();
        });
    }
}