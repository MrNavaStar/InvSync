package mrnavastar.invsync.mixin;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import mrnavastar.invsync.interfaces.IPlayerAdvancementTracker;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.server.ServerAdvancementLoader;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static mrnavastar.invsync.InvSync.advancementLoader;

@Mixin(PlayerAdvancementTracker.class)
public abstract class PlayerAdvancementTrackerMixin implements IPlayerAdvancementTracker {

    @Shadow
    public abstract void clearCriteria();

    @Shadow
    @Final
    private Map<Advancement, AdvancementProgress> progress;

    @Shadow
    @Final
    private Set<Advancement> updatedRoots;

    @Shadow
    @Final
    private Set<Advancement> visibleAdvancements;

    @Shadow
    @Final
    private Set<Advancement> progressUpdates;

    @Shadow
    private boolean dirty;

    @Shadow
    private @Nullable Advancement currentDisplayTab;

    @Shadow
    protected abstract void initProgress(Advancement advancement, AdvancementProgress progress);

    @Shadow
    protected abstract void rewardEmptyAdvancements(ServerAdvancementLoader advancementLoader);

    @Shadow
    protected abstract void beginTrackingAllAdvancements(ServerAdvancementLoader advancementLoader);

    @Shadow
    protected abstract void onStatusUpdate(Advancement advancement);

    @Shadow
    @Final
    private static Gson GSON;

    @Shadow
    @Final
    private static TypeToken<Map<Identifier, AdvancementProgress>> JSON_TYPE;

    @Override
    public void writeAdvancementData(JsonElement advancementData) {
        this.clearCriteria();
        this.progress.clear();
        this.visibleAdvancements.clear();
        this.updatedRoots.clear();
        this.progressUpdates.clear();
        this.dirty = true;
        this.currentDisplayTab = null;

        Map<Identifier, AdvancementProgress> map = GSON.getAdapter(JSON_TYPE).fromJsonTree(advancementData);
        Stream<Map.Entry<Identifier, AdvancementProgress>> stream = map.entrySet().stream().sorted(Map.Entry.comparingByValue());

        for (Map.Entry<Identifier, AdvancementProgress> entry : stream.toList()) {
            Advancement advancement = advancementLoader.get(entry.getKey());
            if (advancement == null) continue;

            this.initProgress(advancement, entry.getValue());
            this.progressUpdates.add(advancement);
            this.onStatusUpdate(advancement);
        }

        this.rewardEmptyAdvancements(advancementLoader);
        this.beginTrackingAllAdvancements(advancementLoader);
    }

    @Override
    public JsonElement readAdvancementData() {
        HashMap<Identifier, AdvancementProgress> map = new HashMap<>();

        for (Map.Entry<Advancement, AdvancementProgress> entry : this.progress.entrySet()) {
            AdvancementProgress advancementProgress = entry.getValue();
            if (advancementProgress.isAnyObtained()) map.put(entry.getKey().getId(), advancementProgress);
        }
        return GSON.toJsonTree(map).getAsJsonObject();
    }
}