package mrnavastar.invsync.mixin;

import mrnavastar.invsync.api.ServerSaveEvent;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {

    @Inject(method = "saveAll", at = @At("HEAD"))
    public void onSave(boolean suppressLogs, boolean flush, boolean force, CallbackInfoReturnable<Boolean> cir) {
        ServerSaveEvent.SAVE_EVERYTHING_EVENT.invoker().onSaveEverything((MinecraftServer) (Object) this);
    }

}
