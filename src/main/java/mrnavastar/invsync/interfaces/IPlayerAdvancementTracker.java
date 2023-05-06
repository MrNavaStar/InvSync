package mrnavastar.invsync.interfaces;

import com.google.gson.JsonElement;

public interface IPlayerAdvancementTracker {
    void writeAdvancementData(JsonElement advancementData);
    JsonElement readAdvancementData();
}
