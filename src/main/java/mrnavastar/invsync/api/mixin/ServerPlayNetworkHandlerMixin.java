package mrnavastar.invsync.api.mixin;

import mrnavastar.invsync.api.events.PlayerLeaveCallback;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
    @Shadow
    public ServerPlayerEntity player;

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;onDisconnect()V"), method = "onDisconnected", cancellable = true)
    private void onPlayerLeave(Text reason, CallbackInfo info) {
        ActionResult result = PlayerLeaveCallback.EVENT.invoker().leaveServer(this.player, this.player.getServer());

        if (result == ActionResult.FAIL) {
            info.cancel();
        }
    }
}
