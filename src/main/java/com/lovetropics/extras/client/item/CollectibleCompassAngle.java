package com.lovetropics.extras.client.item;

import com.lovetropics.extras.ExtraDataComponents;
import com.lovetropics.extras.item.CollectibleCompassItem;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.CompassAngle;
import net.minecraft.client.renderer.item.properties.numeric.CompassAngleState;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import org.jspecify.annotations.Nullable;
import java.util.Optional;

public class CollectibleCompassAngle implements RangeSelectItemModelProperty {
    public static final MapCodec<CollectibleCompassAngle> MAP_CODEC = MapCodec.unit(CollectibleCompassAngle::new);

    private final CompassAngle missingDelegate = new CompassAngle(true, CompassAngleState.CompassTarget.NONE);
    private final CompassAngle recoveryDelegate = new CompassAngle(true, CompassAngleState.CompassTarget.RECOVERY);

    @Override
    public float get(ItemStack stack, @Nullable ClientLevel level, @Nullable ItemOwner owner, int seed) {
        if (owner instanceof Player player) {
            CollectibleCompassItem.Target target = stack.get(ExtraDataComponents.COLLECTIBLE_TARGET);
            if (target != null) {
                // Totally worth it to avoid copying a few lines of code, right? :)
                Optional<GlobalPos> lastDeathLocation = player.getLastDeathLocation();
                player.setLastDeathLocation(Optional.of(target.pos()));
                float angle = recoveryDelegate.get(stack, level, player, seed);
                player.setLastDeathLocation(lastDeathLocation);
                return angle;
            }
        }
        return missingDelegate.get(stack, level, owner, seed);
    }

    @Override
    public MapCodec<CollectibleCompassAngle> type() {
        return MAP_CODEC;
    }
}
