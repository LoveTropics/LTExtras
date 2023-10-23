package com.lovetropics.extras;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class ExtraTags {
    private static final String CREATE_MODID = "create";

    public static class Blocks extends ExtraTags {
        public static final TagKey<Block> STEEL_GIRDERS = modTag("steel_girders");
        public static final TagKey<Block> CREATE_MOVABLE_EMPTY_COLLIDER = tag(CREATE_MODID, "movable_empty_collider");
        public static final TagKey<Block> CLIMBABLE_FAST = modTag("climbable_fast");

        static TagKey<Block> tag(final String modid, final String name) {
            return TagKey.create(Registries.BLOCK, new ResourceLocation(modid, name));
        }

        static TagKey<Block> modTag(final String name) {
            return tag(LTExtras.MODID, name);
        }
    }
}
