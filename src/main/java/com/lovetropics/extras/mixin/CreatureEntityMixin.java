package com.lovetropics.extras.mixin;

import com.lovetropics.extras.block.entity.MobControllerBlockEntity;
import com.lovetropics.extras.entity.ExtendedCreatureEntity;
import com.lovetropics.extras.entity.ai.MoveBackToOriginGoal;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.Constants;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PathfinderMob.class)
public abstract class CreatureEntityMixin extends Mob implements ExtendedCreatureEntity {
    // home
    private boolean theresNoPlaceLikeHome;
    private Vec3 homePos;
    private int homeRange;

    // external controller

    protected CreatureEntityMixin(EntityType<? extends Mob> type, Level worldIn) {
        super(type, worldIn);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);

        if (nbt.contains("TheresNoPlaceLikeHome")) {
            this.theresNoPlaceLikeHome = nbt.getBoolean("TheresNoPlaceLikeHome");

            if (nbt.contains("HomePos")) {
                ListTag posTag = nbt.getList("HomePos", Constants.NBT.TAG_DOUBLE);
                this.homePos = new Vec3(posTag.getDouble(0), posTag.getDouble(1), posTag.getDouble(2));
            } else {
                // Spawn egg or no recorded home- just grab the current position to have something to work with
                this.homePos = this.position();
            }
            // In blocks
            this.homeRange = nbt.contains("HomeRange") ? nbt.getInt("HomeRange") : 20;

            if (this.theresNoPlaceLikeHome) {
                this.goalSelector.addGoal(0, new MoveBackToOriginGoal((PathfinderMob) (Object) this, 1.0, this.homePos, this.homeRange));
            }
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);

        // Don't pollute! Only write if it'll be used
        if (this.theresNoPlaceLikeHome) {
            nbt.putBoolean("TheresNoPlaceLikeHome", this.theresNoPlaceLikeHome);
            nbt.putInt("HomeRange", this.homeRange);

            // vec3d -> nbt
            ListTag pos = new ListTag();
            pos.add(0, DoubleTag.valueOf(this.homePos.x()));
            pos.add(1, DoubleTag.valueOf(this.homePos.y()));
            pos.add(2, DoubleTag.valueOf(this.homePos.z()));

            nbt.put("HomePos", pos);
        }
    }

    @Override
    public void linkToBlockEntity(MobControllerBlockEntity controller) {
        BlockPos pos = controller.getBlockPos();
        this.theresNoPlaceLikeHome = true;
        this.homePos = new Vec3(pos.getX(), pos.getY(), pos.getZ());
        this.homeRange = 32; // static for now
    }
}
