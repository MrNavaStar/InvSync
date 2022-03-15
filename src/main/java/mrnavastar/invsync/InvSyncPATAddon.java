package mrnavastar.invsync;

import com.google.gson.JsonElement;
import net.minecraft.server.ServerAdvancementLoader;

public interface InvSyncPATAddon {

    void INVSYNC$load(ServerAdvancementLoader advancementLoader, JsonElement advancementData);

    JsonElement INVSYNC$save();
}
