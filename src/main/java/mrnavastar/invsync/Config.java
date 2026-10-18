package mrnavastar.invsync;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * config/invsync.json. All servers must resolve sqlite_file to the same database and use matching sync settings.
 * Disabling a sync category preserves its stored data without loading or updating it.
 */
public class Config {
    public String sqlite_file = "/path/to/shared/invsync.db";
    /** Seconds to wait for the previous server to release the player's row before taking over, even if it is still locked. */
    public int lock_timeout_seconds = 10;
    public boolean sync_player_data = true;
    public boolean sync_advancements = true;
    public boolean sync_stats = true;

    static Config load(Path file) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        if (Files.exists(file)) return gson.fromJson(Files.readString(file), Config.class);
        Files.writeString(file, gson.toJson(new Config()));
        return new Config();
    }
}
