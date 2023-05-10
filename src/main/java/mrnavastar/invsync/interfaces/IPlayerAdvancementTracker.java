package mrnavastar.invsync.interfaces;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public interface IPlayerAdvancementTracker {
    void writeAdvancementData(JsonElement advancementData);

    JsonElement readAdvancementData();
}
