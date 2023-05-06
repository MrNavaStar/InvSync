package mrnavastar.invsync.mixin;

import com.mojang.datafixers.DataFixer;
import mrnavastar.invsync.interfaces.IServerStatHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stat.ServerStatHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ServerStatHandler.class)
public abstract class ServerStatHandlerMixin implements IServerStatHandler {

    @Shadow protected abstract String asString();

    @Shadow public abstract void parse(DataFixer dataFixer, String json);

    @Shadow @Final private MinecraftServer server;

    public void writeStatData(String statData) {
        parse(server.getDataFixer(), statData);
    }

    public String readStatData() {
        return asString();
    }
}