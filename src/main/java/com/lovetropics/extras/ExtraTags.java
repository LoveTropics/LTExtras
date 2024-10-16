package com.lovetropics.extras;

import com.lovetropics.extras.collectible.Collectible;
import com.lovetropics.extras.registry.ExtraRegistries;
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

        static TagKey<Block> tag(String modid, String name) {
            return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(modid, name));
        }

        static TagKey<Block> modTag(String name) {
            return tag(LTExtras.MODID, name);
        }
    }

    public static class Items extends ExtraTags {
        public static final TagKey<Item> LIME = tag(TROPICRAFT_ID, "lime");

        static TagKey<Item> tag(String modid, String name) {
            return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(modid, name));
        }

        static TagKey<Item> modTag(String name) {
            return tag(LTExtras.MODID, name);
        }
    }

    public static class Collectibles extends ExtraTags {
        public static final TagKey<Collectible> DONATION_GOAL =  modTag("donation_goal");

        static TagKey<Collectible> modTag(String name) {
            return TagKey.create(ExtraRegistries.COLLECTIBLE, LTExtras.location(name));
        }
    }
}
