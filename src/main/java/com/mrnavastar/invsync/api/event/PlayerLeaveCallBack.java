<<<<<<< HEAD:src/main/java/com/mrnavastar/invsync/api/event/PlayerLeaveCallBack.java
package com.mrnavastar.invsync.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;

public interface PlayerLeaveCallBack {
    Event<PlayerLeaveCallBack> EVENT = EventFactory.createArrayBacked(PlayerLeaveCallBack.class, (listeners) -> (player, server) -> {
        for (PlayerLeaveCallBack listener : listeners) {
            ActionResult result = listener.leaveServer(player, server);

            if (result != ActionResult.PASS) {
                return result;
            }
        }

        return ActionResult.PASS;
    });

    ActionResult leaveServer(ServerPlayerEntity player, MinecraftServer server);
}
=======
package com.mrnavastar.invsync.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;

public interface PlayerLeaveCallBack {
    Event<PlayerLeaveCallBack> EVENT = EventFactory.createArrayBacked(PlayerLeaveCallBack.class, (listeners) -> (player, server) -> {
        for (PlayerLeaveCallBack listener : listeners) {
            ActionResult result = listener.leaveServer(player, server);

            if (result != ActionResult.PASS) {
                return result;
            }
        }

        return ActionResult.PASS;
    });

    ActionResult leaveServer(ServerPlayerEntity player, MinecraftServer server);
}
>>>>>>> 07023deeb41fdb819ebcd94f4511a21104422eee:src/main/java/arnaria/invsync/api/event/PlayerLeaveCallBack.java
