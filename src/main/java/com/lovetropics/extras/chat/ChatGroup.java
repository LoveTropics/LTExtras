package com.lovetropics.extras.chat;

import com.lovetropics.lib.permission.PermissionsApi;
import com.lovetropics.lib.permission.role.RoleOverrideReader;
import com.lovetropics.lib.permission.role.RoleOverrideType;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.serialization.Codec;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.commands.Commands.literal;

@EventBusSubscriber
public record ChatGroup(String id, Component prefix, String command, RoleOverrideType<Boolean> roleOverride) {

    private static final List<ChatGroup> GROUPS = new ArrayList<>();

    private static final ChatGroup ORGANISER = createGroup("organiser", Component.literal("[OG]").withStyle(ChatFormatting.GOLD), "oc");
    private static final ChatGroup MODERATOR = createGroup("moderator", Component.literal("[MOD]").withStyle(ChatFormatting.BLUE), "mc");

    @SubscribeEvent
    public static void onRegisterCommand(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        for (ChatGroup group : GROUPS) {
            ChatGroupCommand.register(dispatcher, group);
        }
    }

    private static class ChatGroupCommand {

        public static void register(CommandDispatcher<CommandSourceStack> dispatcher, ChatGroup group) {
            // @formatter:off
            dispatcher.register(
                    literal(group.command())
                            .requires(Commands.hasPermission(Commands.LEVEL_OWNERS))
                            .then(Commands.argument("message", StringArgumentType.greedyString())
                                            .executes(ctx -> sendMessage(ctx, group))
                            )
            );
            // @formatter:on
        }

        private static int sendMessage(CommandContext<CommandSourceStack> ctx, ChatGroup group) {
            String message = StringArgumentType.getString(ctx, "message");
            CommandSourceStack source = ctx.getSource();
            Component text = group.prefix().copy()
                    .append(" ")
                    .append(Component.literal(message));

            for (ServerPlayer player : source.getServer().getPlayerList().getPlayers()) {
                RoleOverrideReader overrides = PermissionsApi.lookup().byPlayer(player).overrides();
                if (overrides.test(group.roleOverride)) {
                    player.sendSystemMessage(text);
                }
            }
            return Command.SINGLE_SUCCESS;
        }
    }

    private static ChatGroup createGroup(String id, Component prefix, String command) {
        ChatGroup group = new ChatGroup(id, prefix, command, RoleOverrideType.register("chat/" + id, Codec.BOOL));
        GROUPS.add(group);
        return group;
    }
}
