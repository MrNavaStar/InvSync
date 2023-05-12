package mrnavastar.invsync.sync;

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
        System.out.println("Sync manager setting up");
        System.out.println(registeredMods.keySet());

        Table baseTable = registeredMods.get("base");

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> new Thread(() -> {
            System.out.println("INVSYNC: Player join");

            DataContainer baseData = baseTable.get(handler.player.getUuid());
            if (baseData == null) return;

            //Wait until data is free from all other servers
            while (baseData.getBool("dataInUse")) {
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            baseData.put("dataInUse", true);
            invokeLoad(handler.player);
        }, "invsync-load").start());

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            System.out.println("INVSYNC: Player leave");

            invokeSave(handler.player);

            DataContainer baseData = baseTable.get(handler.player.getUuid());
            baseData.put("dataInUse", false);
        });
    }

    public static void registerMod(String modId, Table table) {
        registeredMods.put(modId, table);
    }

    public static void invokeLoad(ServerPlayerEntity player) {
        System.out.println("INVSYNC: loading: " + player);
        registeredMods.forEach((modId, table) -> {
            DataContainer data = table.get(player.getUuid());

            //table.beginTransaction();
            SyncEvents.LOAD_PLAYER_DATA.getInvoker(modId).forEach(modHandler -> modHandler.handle(player, data));
            //table.endTransaction();
        });
    }

    public static void invokeSave(ServerPlayerEntity player) {
        System.out.println("INVSYNC: saving: " + player);
        registeredMods.forEach((modId, table) -> {
            System.out.println("here??????");
            DataContainer data = Objects.requireNonNullElse(table.get(player.getUuid()), table.createDataContainer(player.getUuid()));
            System.out.println("saving??????");
            table.beginTransaction();

            SyncEvents.SAVE_PLAYER_DATA.getInvoker(modId).forEach(modHandler -> modHandler.handle(player, data));
            table.endTransaction();
        });
    }
}