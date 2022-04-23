package com.lovetropics.extras.mixin;

import com.lovetropics.extras.block.entity.MobControllerBlockEntity;
import com.lovetropics.extras.entity.ExtendedCreatureEntity;
import com.lovetropics.extras.entity.ai.MoveBackToOriginGoal;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.DoubleNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CreatureEntity.class)
public abstract class CreatureEntityMixin extends MobEntity implements ExtendedCreatureEntity {
    // home
    private boolean theresNoPlaceLikeHome;
    private Vector3d homePos;
    private int homeRange;

    // external controller

    protected CreatureEntityMixin(EntityType<? extends MobEntity> type, World worldIn) {
        super(type, worldIn);
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT nbt) {
        super.readAdditionalSaveData(nbt);

        if (nbt.contains("TheresNoPlaceLikeHome")) {
            this.theresNoPlaceLikeHome = nbt.getBoolean("TheresNoPlaceLikeHome");

            if (nbt.contains("HomePos")) {
                ListNBT posTag = nbt.getList("HomePos", Constants.NBT.TAG_DOUBLE);
                this.homePos = new Vector3d(posTag.getDouble(0), posTag.getDouble(1), posTag.getDouble(2));
            } else {
                // Spawn egg or no recorded home- just grab the current position to have something to work with
                this.homePos = this.position();
            }
            // In blocks
            this.homeRange = nbt.contains("HomeRange") ? nbt.getInt("HomeRange") : 20;

            if (this.theresNoPlaceLikeHome) {
                this.goalSelector.addGoal(0, new MoveBackToOriginGoal((CreatureEntity) (Object) this, 1.0, this.homePos, this.homeRange));
            }
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT nbt) {
        super.addAdditionalSaveData(nbt);

        // Don't pollute! Only write if it'll be used
        if (this.theresNoPlaceLikeHome) {
            nbt.putBoolean("TheresNoPlaceLikeHome", this.theresNoPlaceLikeHome);
            nbt.putInt("HomeRange", this.homeRange);

            // vec3d -> nbt
            ListNBT pos = new ListNBT();
            pos.add(0, DoubleNBT.valueOf(this.homePos.x()));
            pos.add(1, DoubleNBT.valueOf(this.homePos.y()));
            pos.add(2, DoubleNBT.valueOf(this.homePos.z()));

            nbt.put("HomePos", pos);
        }
    }

    @Override
    public void linkToBlockEntity(MobControllerBlockEntity controller) {
        BlockPos pos = controller.getBlockPos();
        this.theresNoPlaceLikeHome = true;
        this.homePos = new Vector3d(pos.getX(), pos.getY(), pos.getZ());
        this.homeRange = 32; // static for now
    }
}
