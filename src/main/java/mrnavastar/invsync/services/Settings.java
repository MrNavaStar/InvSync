package mrnavastar.invsync.services;

import mc.microconfig.Comment;
import mc.microconfig.ConfigData;


public class Settings implements ConfigData {
    @Comment("Allowed Values: \"SQLITE\" | \"MYSQL\"")
    public String DATABASE_TYPE = "SQLITE";
    public String DATABASE_NAME = "InvSync";

    public String SQLITE_DIRECTORY = "/path/to/folder";

    public String MYSQL_ADDRESS = "127.0.0.1";
    public String MYSQL_PORT = "3306";
    @Comment("The mod will not start if you use these as your actual credentials - please keep your data secure")
    public String MYSQL_USERNAME = "username";
    public String MYSQL_PASSWORD = "password";

    @Comment("Sync Settings")
    public boolean SYNC_PLAYER_DATA = true;
    public boolean SYNC_ADVANCEMENTS = true;
    public boolean SYNC_STATS = true;
}