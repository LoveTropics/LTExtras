package com.lovetropics.extras.client.command;

import com.lovetropics.extras.LTExtras;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderNameTagEvent;
import net.neoforged.neoforge.common.util.TriState;

import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static net.minecraft.commands.Commands.literal;

@EventBusSubscriber(modid = LTExtras.MODID, value = Dist.CLIENT)
public class RenderPlayerNameTagCommand {
    private static boolean alwaysRenderNameTags = false;

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("alwaysRenderPlayerNametags")
                .executes(RenderPlayerNameTagCommand::toggleAlwaysRender)
                .then(Commands.argument("state", bool())
                        .executes(RenderPlayerNameTagCommand::setAlwaysRender)
                ));
    }

    public static int toggleAlwaysRender(final CommandContext<CommandSourceStack> ctx) {
        setSend(ctx, !alwaysRenderNameTags);
        return Command.SINGLE_SUCCESS;
    }

    public static int setAlwaysRender(final CommandContext<CommandSourceStack> ctx) {
        setSend(ctx, BoolArgumentType.getBool(ctx, "state"));
        return Command.SINGLE_SUCCESS;
    }

    private static void setSend(final CommandContext<CommandSourceStack> ctx, final boolean newValue) {
        alwaysRenderNameTags = newValue;
        ctx.getSource().sendSuccess(() -> Component.translatable("commands.nametag.success", alwaysRenderNameTags), false);
    }

    @SubscribeEvent
    public static void onRenderNameTagEvent(RenderNameTagEvent evt) {
        if (alwaysRenderNameTags && evt.getEntity() instanceof Player) {
            evt.setCanRender(TriState.TRUE);
        }
    }

    public static void addTranslations(final RegistrateLangProvider provider) {
        provider.add("commands.nametag.success", "Always render nametags: %s");
    }
}
