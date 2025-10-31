package com.lovetropics.extras.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;

import java.util.List;

public record InteractActionData(List<ClickEvent> clickEvents, boolean cancelEvent) {

    public static final Codec<InteractActionData> CODEC = RecordCodecBuilder.create(i -> i.group(
            ExtraCodecs.compactListCodec(ClickEvent.CODEC).fieldOf("click_events").forGetter(InteractActionData::clickEvents),
            Codec.BOOL.optionalFieldOf("cancel_event", false).forGetter(InteractActionData::cancelEvent)
    ).apply(i, InteractActionData::new));

    public void performCommands(ServerPlayer player) {
        for (ClickEvent clickEvent : clickEvents) {
            switch (clickEvent) {
                case ClickEvent.RunCommand runCommand:
                    CommandSourceStack targetSource = player.createCommandSourceStack().withPermission(2);
                    player.level().getServer()
                            .getCommands()
                            .performPrefixedCommand(targetSource, runCommand.command());
                    break;
                case ClickEvent.ShowDialog showDialog:
                    player.openDialog(showDialog.dialog());
                    break;
                case ClickEvent.Custom custom:
                    player.level().getServer().handleCustomClickAction(custom.id(), custom.payload());
                    break;
                default:
            }
        }
    }
}
