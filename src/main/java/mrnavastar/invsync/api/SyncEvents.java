package mrnavastar.invsync.api;

import mrnavastar.sqlib.api.DataContainer;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;

public class SyncEvents {

    public static final Event<Fetch> FETCH_PLAYER_DATA = EventFactory.createArrayBacked(Fetch.class, callbacks -> (player, playerData) -> {
        for (Fetch callback : callbacks) {
            if (playerData != null) callback.fetch(player, playerData);
        }
    });

    public static final Event<Save> SAVE_PLAYER_DATA = EventFactory.createArrayBacked(Save.class, callbacks -> (player, playerData) -> {
        for (Save callback : callbacks) {
            callback.save(player, playerData);
        }
    });

    @FunctionalInterface
    public interface Fetch {
        void fetch(ServerPlayerEntity player, DataContainer playerData);
    }

    @FunctionalInterface
    public interface Save {
        void save(ServerPlayerEntity player, DataContainer playerData);
    }
}
