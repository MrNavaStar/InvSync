package mrnavastar.invsync.mixin;

import mrnavastar.invsync.api.ServerSyncEvents;
import mrnavastar.sqlib.api.DataContainer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static mrnavastar.invsync.InvSync.playerData;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {

    @Shadow public abstract PlayerManager getPlayerManager();

    @Inject(method = "saveAll", at = @At("HEAD"))
    public void onSave(boolean suppressLogs, boolean flush, boolean force, CallbackInfoReturnable<Boolean> cir) {
        playerData.beginTransaction();
        this.getPlayerManager().getPlayerList().forEach(player -> {
            DataContainer playerDataContainer = playerData.get(player.getUuid());
            if (playerDataContainer == null) {
                playerDataContainer = playerData.createDataContainer(player.getUuid());
                playerData.put(playerDataContainer);
            }
            ServerSyncEvents.SAVE_PLAYER_DATA.invoker().handle(player, playerDataContainer);
        });
        playerData.endTransaction();
    }
}