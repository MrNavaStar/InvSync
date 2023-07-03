package mrnavastar.invsync.sync;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mrnavastar.sqlib.DataContainer;
import mrnavastar.sqlib.Table;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.nbt.NbtCompound;
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

            invokeSave(SyncEvents.SAVE_PLAYER_DATA, handler.player);

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

    public static void loadAdvancementData(ServerPlayerEntity player, JsonObject advancementData) {
        registeredMods.forEach((modId, table) -> {
            DataContainer container = table.get(player.getUuid());

            //table.beginTransaction();
            SyncEvents.LOAD_ADVANCEMENT_DATA.getInvoker(modId).forEach(modHandler -> modHandler.handle(player, advancementData, container));
            //table.endTransaction();
        });
    }

    public static void savePlayerData(ServerPlayerEntity player) {
        registeredMods.forEach((modId, table) -> {
            DataContainer container = Objects.requireNonNullElse(table.get(player.getUuid()), table.createDataContainer(player.getUuid()));
            //table.beginTransaction();

            NbtCompound nbt = new NbtCompound();
            player.writeNbt(nbt);
            SyncEvents.SAVE_PLAYER_DATA.getInvoker(modId).forEach(modHandler -> modHandler.handle(player, nbt, container));
            //table.endTransaction();
        });
    }

    public static void saveAdvancementData(ServerPlayerEntity player, JsonObject advancementData) {
        registeredMods.forEach((modId, table) -> {
            DataContainer container = Objects.requireNonNullElse(table.get(player.getUuid()), table.createDataContainer(player.getUuid()));
            //table.beginTransaction();

            SyncEvents.SAVE_ADVANCEMENT_DATA.getInvoker(modId).forEach(modHandler -> modHandler.handle(player, advancementData, container));
            //table.endTransaction();
        });
    }

}