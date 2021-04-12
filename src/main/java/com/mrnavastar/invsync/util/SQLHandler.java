package mrnavastar.friendlist.commands;

import mrnavastar.friendlist.api.interfaces.PlayerEntityExt;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import mrnavastar.friendlist.util.PlayerCache;
import net.minecraft.command.CommandSource;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;

public class FriendsCommand {

    public static void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("friends")

                //Add friend
                .then(CommandManager.literal("add")
                .then(CommandManager.argument("player", StringArgumentType.word()).suggests((commandContext, suggestionsBuilder) -> {
                    String executor = commandContext.getSource().getPlayer().getName().asString();
                    PlayerManager playerManager = commandContext.getSource().getMinecraftServer().getPlayerManager();
                    return CommandSource.suggestMatching(playerManager.getPlayerList().stream()
                            .filter((serverPlayerEntity -> !executor.equals(serverPlayerEntity.getEntityName())))
                            .map((serverPlayerEntity) -> serverPlayerEntity.getGameProfile().getName()), suggestionsBuilder);
                })
                    .executes(command -> addFriend(command, getString(command, "player")))))

                //Remove Friend
                .then(CommandManager.literal("remove")
                .then(CommandManager.argument("player", StringArgumentType.word()).suggests((commandContext, suggestionsBuilder) -> {
                    PlayerEntity executor = commandContext.getSource().getPlayer();
                    ArrayList<UUID> friendsList = ((PlayerEntityExt) executor).getFriendUUIDList();
                    return CommandSource.suggestMatching(friendsList.stream().map(PlayerCache::getNameFromUuid), suggestionsBuilder);
                })
                    .executes(command -> removeFriend(command, getString(command, "player")))))

                //View friend list
                .then(CommandManager.literal("list")
                    .executes(FriendsCommand::getFriendList))

                //Admin view cache & dump cache
                .requires(source -> source.hasPermissionLevel(4))
                .then(CommandManager.literal("cache")
                .then(CommandManager.literal("view")
                    .executes(FriendsCommand::getCache))
                .then(CommandManager.literal("dump")
                    .executes(FriendsCommand::dumpCache))));

        //DEBUG
        dispatcher.register(CommandManager.literal("friends")
                .then(CommandManager.literal("wipe")
                .executes(FriendsCommand::wipeAll)));
    }

    public static int addFriend(CommandContext<ServerCommandSource> command, String name) throws CommandSyntaxException {
        final PlayerEntity executor = command.getSource().getPlayer();
        final PlayerEntity target = command.getSource().getMinecraftServer().getPlayerManager().getPlayer(name);
        final UUID targetUuid = PlayerCache.getUuidFromName(name);

        if (!executor.getEntityName().equals(name)) {
            if (targetUuid != null && !((PlayerEntityExt) executor).getFriendUUIDList().contains(targetUuid)) {
                ((PlayerEntityExt) executor).addFriend(targetUuid);
                if (target != null)
                    target.sendMessage(new LiteralText(executor.getEntityName() + " wants to add you as a friend! CLICK: ACCEPT | DECLINE"), false);
            } else {
                executor.sendMessage(new LiteralText("no player found"), false);
            }
        }
        return 1;
    }

    public static int removeFriend(CommandContext<ServerCommandSource> command, String name) throws CommandSyntaxException {
        final PlayerEntity executor = command.getSource().getPlayer();
        final UUID targetUuid = PlayerCache.getUuidFromName(name);

        if (!executor.getEntityName().equals(name)) {
            if (((PlayerEntityExt) executor).getFriendUUIDList().contains(targetUuid)) {
                ((PlayerEntityExt) executor).removeFriend(targetUuid);
                executor.sendMessage(new LiteralText(name + " is no longer your friend"), false);
            }
        }
        return 1;
    }

    public static int getFriendList(CommandContext<ServerCommandSource> command) throws CommandSyntaxException {
        final PlayerEntity executor = command.getSource().getPlayer();
        StringBuilder friends = new StringBuilder(), requests = new StringBuilder();

        for (UUID uuid : ((PlayerEntityExt) executor).getFriendUUIDList()) {
            friends.append(Objects.requireNonNull(PlayerCache.getNameFromUuid(uuid))).append(", ");
        }

        /*for (UUID uuid : ((PlayerEntityExt) executor).getRequestUUIDtList()) {
            requests.append(Objects.requireNonNull(APIHandler.getPlayerName(uuid))).append(", ");
        }*/

        String friendsCombined = "None", requestsCombined = "None";
        if (friends.length() > 1) friendsCombined = friends.substring(0, friends.length() - 2);
        //if (requests.length() > 1) requestsCombined = requests.substring(0, requests.length() - 2);

        executor.sendMessage(new LiteralText("Friends: " + friendsCombined), false);
        //executor.sendMessage(new LiteralText("Pending: " + requestsCombined), false);
        return 1;
    }

    //DEBUG
    public static int wipeAll(CommandContext<ServerCommandSource> command) throws CommandSyntaxException  {
        final PlayerEntity executor = command.getSource().getPlayer();
        ((PlayerEntityExt) executor).wipeAll();
        return 1;
    }

    public static int dumpCache(CommandContext<ServerCommandSource> command) throws CommandSyntaxException {
        final PlayerEntity executor = command.getSource().getPlayer();
        PlayerCache.dumpCache();
        executor.sendMessage(new LiteralText("Player cache has been cleared"), false);
        return 1;
    }

    public static int getCache(CommandContext<ServerCommandSource> command) throws CommandSyntaxException {
        final PlayerEntity executor = command.getSource().getPlayer();
        executor.sendMessage(new LiteralText(PlayerCache.getCache().toString()), false);
        return 1;
    }
}
