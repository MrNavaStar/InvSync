package mrnavastar.invsync.sync;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mrnavastar.sqlib.DataContainer;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.HashMap;

public class SyncEvents {

    public static class Event<T> {
        private final HashMap<String, ArrayList<T>> handlers = new HashMap<>();

        public void register(String modId, T handler) {
            ArrayList<T> modHandlers = handlers.computeIfAbsent(modId, k -> new ArrayList<>());
            modHandlers.add(handler);
        }

        public ArrayList<T> getInvoker(String modId) {
            return handlers.computeIfAbsent(modId, k -> new ArrayList<>());
        }
    }

    public static final Event<PlayerDataHandler> LOAD_PLAYER_DATA = new Event<>();
    public static final Event<PlayerDataHandler> SAVE_PLAYER_DATA = new Event<>();

    public static final Event<AdvancementDataHandler> LOAD_ADVANCEMENT_DATA = new Event<>();
    public static final Event<AdvancementDataHandler> SAVE_ADVANCEMENT_DATA = new Event<>();

    @FunctionalInterface
    public interface PlayerDataHandler {
        void handle(ServerPlayerEntity player, NbtCompound playerData, DataContainer dataContainer);
    }

    @FunctionalInterface
    public interface AdvancementDataHandler {
        void handle(ServerPlayerEntity player, JsonObject advancementData, DataContainer dataContainer);
    }
}