package mrnavastar.invsync.services;

import mrnavastar.sqlib.DataContainer;
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

    public static final Event<SyncHandler> LOAD_PLAYER_DATA = new Event<>();
    public static final Event<SyncHandler> SAVE_PLAYER_DATA = new Event<>();

    @FunctionalInterface
    public interface SyncHandler {
        void handle(ServerPlayerEntity player, DataContainer data);
    }
}