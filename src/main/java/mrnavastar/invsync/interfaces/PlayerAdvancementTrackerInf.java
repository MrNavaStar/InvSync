package mrnavastar.invsync.interfaces;

import com.google.gson.JsonElement;

public interface PlayerAdvancementTrackerInf {
    void writeAdvancementData(JsonElement advancementData);
    JsonElement readAdvancementData();
}
