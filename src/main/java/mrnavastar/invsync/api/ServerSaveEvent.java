package mrnavastar.invsync.api;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.MinecraftServer;

public class ServerSaveEvent {

    public static final Event<SaveEverything> SAVE_EVERYTHING_EVENT = EventFactory.createArrayBacked(SaveEverything.class, calls -> server -> {
        for (SaveEverything call : calls) {
            call.onSaveEverything(server);
        }
    });

    @FunctionalInterface
    public interface SaveEverything {
        void onSaveEverything(MinecraftServer server);
    }

}
