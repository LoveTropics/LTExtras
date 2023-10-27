package com.lovetropics.extras.item;

import com.lovetropics.extras.ExtraLangKeys;
import com.lovetropics.extras.LTExtras;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class InviteItem extends Item {
    public static final List<ImageData> PRESETS = List.of(
            new ImageData(
                    Optional.of(Component.literal("1").withStyle(ChatFormatting.DARK_PURPLE)),
                    new ResourceLocation(LTExtras.MODID, "textures/images/ccfucc_invite_1.png"),
                    371,
                    292,
                    List.of(
                            ImageData.text(ExtraLangKeys.CLUB_INVITE_1_TOP.get().withStyle(s -> s.withColor(0xd6b3e0)), 287, 153)
                                    .align(ImageData.Align.CENTER, ImageData.Align.END)
                                    .lineSpacing(18)
                                    .maxWidth(125),
                            ImageData.text(ExtraLangKeys.CLUB_INVITE_1_BOTTOM.get().withStyle(s -> s.withColor(0xd6b3e0)), 287, 191)
                                    .align(ImageData.Align.CENTER, ImageData.Align.START)
                                    .lineSpacing(18)
                                    .maxWidth(125)
                    )
            ),
            new ImageData(
                    Optional.of(Component.literal("2").withStyle(ChatFormatting.DARK_PURPLE)),
                    new ResourceLocation(LTExtras.MODID, "textures/images/ccfucc_invite_2.png"),
                    371,
                    292,
                    List.of(
                            ImageData.text(ExtraLangKeys.CLUB_INVITE_2_TOP.get().withStyle(s -> s.withColor(0xd6b3e0)), 287, 153)
                                    .align(ImageData.Align.CENTER, ImageData.Align.END)
                                    .lineSpacing(18)
                                    .maxWidth(125),
                            ImageData.text(ExtraLangKeys.CLUB_INVITE_2_BOTTOM.get().withStyle(s -> s.withColor(0xd6b3e0)), 287, 191)
                                    .align(ImageData.Align.CENTER, ImageData.Align.START)
                                    .lineSpacing(18)
                                    .maxWidth(135)
                    )
            )
    );

    public InviteItem(final Properties properties) {
        super(properties);
    }

    @Override
    public Component getName(final ItemStack stack) {
        return super.getName(stack).copy().withStyle(ChatFormatting.OBFUSCATED);
    }

    @Override
    public void appendHoverText(final ItemStack stack, @Nullable final Level level, final List<Component> lines, final TooltipFlag flag) {
        super.appendHoverText(stack, level, lines, flag);
        ImageData.get(stack).flatMap(ImageData::name).ifPresent(lines::add);
    }
}
