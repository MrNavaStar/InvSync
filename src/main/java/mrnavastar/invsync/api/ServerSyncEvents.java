package mrnavastar.invsync.api;

import mrnavastar.sqlib.api.DataContainer;
import mrnavastar.sqlib.api.Table;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

public class ServerSyncEvents {

    public static final Event<PlayerDataHandler> FETCH_PLAYER_DATA = EventFactory.createArrayBacked(PlayerDataHandler.class, callbacks -> (player, playerData) -> {
        for (PlayerDataHandler callback : callbacks) {
            if (playerData != null) callback.handle(player, playerData);
        }
    });

    public static final Event<PlayerDataHandler> SAVE_PLAYER_DATA = EventFactory.createArrayBacked(PlayerDataHandler.class, callbacks -> (player, playerData) -> {
        for (PlayerDataHandler callback : callbacks) {
            callback.handle(player, playerData);
        }
    });

    public static final Event<ServerDataHandler> FETCH_SERVER_DATA = EventFactory.createArrayBacked(ServerDataHandler.class, callbacks -> (server, serverData) -> {
       for (ServerDataHandler callback : callbacks) {
           if (serverData != null) callback.handle(server, serverData);
       }
    });

    public static final Event<ServerDataHandler> SAVE_SERVER_DATA = EventFactory.createArrayBacked(ServerDataHandler.class, callbacks -> (server, serverData) -> {
        for (ServerDataHandler callback : callbacks) {
            callback.handle(server, serverData);
        }
    });

    @FunctionalInterface
    public interface PlayerDataHandler {
        void handle(ServerPlayerEntity player, DataContainer playerData);
    }

    @FunctionalInterface
    public interface ServerDataHandler {
        void handle(MinecraftServer server, Table serverData);
    }
}
