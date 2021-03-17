package arnaria.invsync;

import arnaria.invsync.api.event.PlayerJoinCallBack;
import arnaria.invsync.api.event.PlayerLeaveCallBack;
import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

public class Invsync implements ModInitializer {

    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_NAME = "invsync";

    @Override
    public void onInitialize() {
        log(Level.INFO, "Initializing");

        PlayerJoinCallBack.EVENT.register((player, server) -> {
            log(Level.INFO, "Getting Player Data From SQL Server");
            return ActionResult.PASS;
        });

        PlayerLeaveCallBack.EVENT.register((player, server) -> {
            log(Level.INFO, "Saving Player Data to SQL Server");
            NBTtoSQL.convert(player);
            return ActionResult.PASS;
        });

        log(Level.INFO, "Done");
    }

    public static void log(Level level, String message) {
        log(level, message, (Object) null);
    }

    public static void log(Level level, String message, Object ... fields){
        LOGGER.log(level, "["+MOD_NAME+"] " + message, fields);
    }
}
