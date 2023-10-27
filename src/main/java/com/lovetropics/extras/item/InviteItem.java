package com.lovetropics.extras.item;

import com.lovetropics.extras.ExtraLangKeys;
import com.lovetropics.extras.LTExtras;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

public class InviteItem extends Item {
    public static final List<ImageData> PRESETS = List.of(
            new ImageData(
                    Optional.of(inviteName("1")),
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
                    Optional.of(inviteName("2")),
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

    private static MutableComponent inviteName(final String number) {
        return Component.empty()
                .append(Component.literal("...").withStyle(ChatFormatting.OBFUSCATED))
                .append(Component.literal(" " + number + " ").withStyle(ChatFormatting.DARK_PURPLE))
                .append(Component.literal("...").withStyle(ChatFormatting.OBFUSCATED));
    }

    public InviteItem(final Properties properties) {
        super(properties);
    }

    @Override
    public Component getName(final ItemStack stack) {
        final Optional<Component> name = ImageData.get(stack).flatMap(ImageData::name);
        if (name.isPresent()) {
            return name.get();
        }
        return super.getName(stack).copy().withStyle(ChatFormatting.OBFUSCATED);
    }
}
