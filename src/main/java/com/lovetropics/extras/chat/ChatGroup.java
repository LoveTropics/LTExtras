package com.lovetropics.extras.chat;

import com.lovetropics.extras.client.command.NameTagModeCommand;
import com.lovetropics.lib.permission.PermissionsApi;
import com.lovetropics.lib.permission.role.RoleOverrideReader;
import com.lovetropics.lib.permission.role.RoleOverrideType;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.serialization.Codec;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.minecraft.commands.Commands.literal;

@EventBusSubscriber
public record ChatGroup(String id, String prefix, TextColor color, List<String> commands, RoleOverrideType<Boolean> roleOverride) {

    private static final List<ChatGroup> GROUPS = new ArrayList<>();

    private static final ChatGroup ORGANISER = createGroup("organiser", "[ORG]", TextColor.GOLD,  "oc", "og", "org");
    private static final ChatGroup MODERATOR = createGroup("moderator", "[MOD]", TextColor.DARK_AQUA, "mc", "mod");

    private static final RoleOverrideType<Boolean> NO_CHAT = RoleOverrideType.register("chat/no_chat", Codec.BOOL);

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
            for (String command : group.commands()) {
                dispatcher.register(
                        literal(command)
                                .requires(Commands.hasPermission(Commands.LEVEL_OWNERS))
                                .then(Commands.argument("message", StringArgumentType.greedyString())
                                        .executes(ctx -> sendMessage(ctx, group))
                                )
                );
            }
            // @formatter:on
        }

        private static int sendMessage(CommandContext<CommandSourceStack> ctx, ChatGroup group) {
            String message = StringArgumentType.getString(ctx, "message");
            CommandSourceStack source = ctx.getSource();
            Component text = Component.literal(group.prefix()).withColor(group.color())
                    .append(Component.literal(" <").withStyle(ChatFormatting.WHITE))
                    .append(ctx.getSource().getDisplayName())
                    .append(Component.literal("> ").withStyle(ChatFormatting.WHITE))
                    .append(Component.literal(message).withColor(group.color()));

            for (ServerPlayer player : source.getServer().getPlayerList().getPlayers()) {
                RoleOverrideReader overrides = PermissionsApi.lookup().byPlayer(player).overrides();
                if (overrides.test(NO_CHAT)) {
                    continue;
                }
                
                if (overrides.test(group.roleOverride)) {
                    player.sendSystemMessage(text);
                }
            }

            handlePings(message, ctx.getSource().getServer(), group);

            return Command.SINGLE_SUCCESS;
        }
    }

    private static void handlePings(String message, MinecraftServer server, ChatGroup group) {
        String lowerMessage = message.toLowerCase();
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (lowerMessage.contains(player.nameAndId().name().toLowerCase())) {
                RoleOverrideReader overrides = PermissionsApi.lookup().byPlayer(player).overrides();
                if (overrides.test(NO_CHAT)) {
                    continue;
                }

                if (overrides.test(group.roleOverride)) {
                    sendNotifySound(player, BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.BELL_BLOCK), SoundSource.PLAYERS, 1.0f, 1.0f);
                }
            }
        }
    }

    private static ChatGroup createGroup(String id, String prefix, TextColor color, String... command) {
        ChatGroup group = new ChatGroup(id, prefix, color, List.of(command), RoleOverrideType.register("chat/" + id, Codec.BOOL));
        GROUPS.add(group);
        return group;
    }


    public static void sendNotifySound(ServerPlayer serverPlayer, Holder<SoundEvent> soundEvent, SoundSource soundSource, float f, float g) {
        serverPlayer.connection.send(new ClientboundSoundPacket(
                soundEvent,
                soundSource, serverPlayer.getX(),
                serverPlayer.getY(),
                serverPlayer.getZ(),
                f,
                g,
                serverPlayer.getRandom().nextLong()));
    }
}
