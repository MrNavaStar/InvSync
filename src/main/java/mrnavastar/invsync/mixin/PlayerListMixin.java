package mrnavastar.invsync.mixin;

import mrnavastar.invsync.InvSync;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Every way a player leaves (real disconnect, fake player kill, shutdown) goes through PlayerList.remove. */
@Mixin(PlayerList.class)
public abstract class PlayerListMixin {
    @Inject(method = "remove", at = @At("HEAD"))
    private void invsync$onLeave(ServerPlayer player, CallbackInfo ci) {
        InvSync.onLeave(player);
    }
}
