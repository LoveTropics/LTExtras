package com.lovetropics.extras.mixin;

import com.lovetropics.extras.block.entity.MobControllerBlockEntity;
import com.lovetropics.extras.entity.ExtendedCreatureEntity;
import com.lovetropics.extras.entity.ai.MoveBackToOriginGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PathfinderMob.class)
public abstract class CreatureEntityMixin extends Mob implements ExtendedCreatureEntity {
    // home
    private boolean theresNoPlaceLikeHome;
    private @Nullable Vec3 homePos;
    private int homeRange;

    // external controller

    protected CreatureEntityMixin(EntityType<? extends Mob> type, Level worldIn) {
        super(type, worldIn);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);

        theresNoPlaceLikeHome = input.getBooleanOr("TheresNoPlaceLikeHome", false);

        if (theresNoPlaceLikeHome) {
            // Spawn egg or no recorded home- just grab the current position to have something to work with
            homePos = input.read("HomePos", Vec3.CODEC).orElse(position());
            // In blocks
            homeRange = input.getIntOr("HomeRange", 20);

            goalSelector.addGoal(0, new MoveBackToOriginGoal((PathfinderMob) (Object) this, 1.0, homePos, homeRange));
        }
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);

        // Don't pollute! Only write if it'll be used
        if (theresNoPlaceLikeHome) {
            output.putBoolean("TheresNoPlaceLikeHome", true);
            output.putInt("HomeRange", homeRange);
            output.storeNullable("HomePos", Vec3.CODEC, homePos);
        }
    }

    @Override
    public void linkToBlockEntity(MobControllerBlockEntity controller) {
        BlockPos pos = controller.getBlockPos();
        theresNoPlaceLikeHome = true;
        homePos = new Vec3(pos.getX(), pos.getY(), pos.getZ());
        homeRange = 32; // static for now
    }
}
