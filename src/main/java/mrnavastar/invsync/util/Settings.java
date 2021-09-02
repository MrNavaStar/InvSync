package mrnavastar.invsync.util;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import mrnavastar.invsync.Invsync;
import mrnavastar.sqlib.api.SqlTypes;

@Config(name = Invsync.MODID)
public class Settings implements ConfigData {
   @Comment("Allowed Values: \"SQLITE\" | \"MYSQL\"")
   public String DATABASE_TYPE = SqlTypes.SQLITE;
   public String DATABASE_NAME = "InvSync";

   public String SQLITE_DIRECTORY = "/path/to/folder";

   public String MYSQL_ADDRESS = "127.0.0.1";
   public String MYSQL_PORT = "3306";
   @Comment("The mod will not start if you use these as your actual credentials - please keep your data secure")
   public String MYSQL_USERNAME = "username";
   public String MYSQL_PASSWORD = "password";

   @Comment("Sync Settings")
   public boolean SYNC_CREATIVE_MODE = true;
   public boolean SYNC_INVENTORY = true;
   public boolean SYNC_ENDER_CHEST = true;
   public boolean SYNC_HEALTH = true;
   public boolean SYNC_FOOD_LEVEL = true;
   public boolean SYNC_XP_LEVEL = true;
   public boolean SYNC_SCORE = true;
   public boolean SYNC_STATUS_EFFECTS = true;
}