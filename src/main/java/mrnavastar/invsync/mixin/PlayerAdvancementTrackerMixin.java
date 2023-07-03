package mrnavastar.invsync.mixin;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mrnavastar.invsync.sync.SyncManager;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Map;

@Mixin(PlayerAdvancementTracker.class)
public class PlayerAdvancementTrackerMixin {

    @Shadow private ServerPlayerEntity owner;

    @Inject(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/PathUtil;createDirectories(Ljava/nio/file/Path;)V"), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    public void load() {
        JsonObject advancementData = new JsonObject();
        SyncManager.loadAdvancementData(owner, advancementData);

    }

    @Inject(method = "save", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/PathUtil;createDirectories(Ljava/nio/file/Path;)V"), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    public void save(CallbackInfo ci, Map map, JsonElement jsonElement) {
        SyncManager.saveAdvancementData(owner, jsonElement.getAsJsonObject());
        ci.cancel();
    }
}
