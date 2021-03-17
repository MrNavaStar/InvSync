package arnaria.invsync.api.mixin;

import arnaria.invsync.api.event.PlayerJoinCallBack;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class playerJoinMixin {
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;onSpawn()V"), method = "onPlayerConnect", cancellable = true)
    private  void onPlayerJoin(ClientConnection connection, ServerPlayerEntity player, CallbackInfo info) {
        ActionResult result = PlayerJoinCallBack.EVENT.invoker().joinServer(player, player.getServer());

        if (result == ActionResult.FAIL) {
            info.cancel();
        }
    }
}
