package com.lovetropics.extras;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ExtraTags {
    private static final String CREATE_MODID = "create";
    private static final String TROPICRAFT_ID = "tropicraft";

    public static class Blocks extends ExtraTags {
        public static final TagKey<Block> STEEL_GIRDERS = modTag("steel_girders");
        public static final TagKey<Block> CREATE_MOVABLE_EMPTY_COLLIDER = tag(CREATE_MODID, "movable_empty_collider");
        public static final TagKey<Block> CLIMBABLE_FAST = modTag("climbable_fast");
        public static final TagKey<Block> CLIMBABLE_VERY_FAST = modTag("climbable_very_fast");

        static TagKey<Block> tag(final String modid, final String name) {
            return TagKey.create(Registries.BLOCK, new ResourceLocation(modid, name));
        }

        static TagKey<Block> modTag(final String name) {
            return tag(LTExtras.MODID, name);
        }
    }

    public static class Items extends ExtraTags {
        public static final TagKey<Item> LIME = tag(TROPICRAFT_ID, "lime");

        static TagKey<Item> tag(final String modid, final String name) {
            return TagKey.create(Registries.ITEM, new ResourceLocation(modid, name));
        }

        static TagKey<Item> modTag(final String name) {
            return tag(LTExtras.MODID, name);
        }
    }
}
