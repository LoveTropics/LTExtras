package com.lovetropics.extras.entity;

import com.lovetropics.extras.ExtraBlocks;
import com.lovetropics.extras.ExtraTags;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import org.jspecify.annotations.Nullable;
import java.util.Optional;

public class PrimedPlumbersTnt extends PrimedTnt {

    private static final BlockState DEFAULT_BLOCK_STATE = ExtraBlocks.PLUMBERS_TNT.getDefaultState();
    private static final EntityDataAccessor<Integer> DATA_FUSE_ID = SynchedEntityData.defineId(PrimedPlumbersTnt.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<BlockState> DATA_BLOCK_STATE_ID = SynchedEntityData.defineId(PrimedPlumbersTnt.class, EntityDataSerializers.BLOCK_STATE);

    private static final ExplosionDamageCalculator DAMAGE_CALCULATOR = new ExplosionDamageCalculator() {
        public boolean shouldBlockExplode(Explosion explosion, BlockGetter level, BlockPos pos, BlockState state, float power) {
            return state.is(ExtraTags.Blocks.PLUMBERS_TNT_EXPLODES) || state.is(ExtraBlocks.PLUMBERS_TNT.get());
        }

        public Optional<Float> getBlockExplosionResistance(Explosion explosion, BlockGetter level, BlockPos pos, BlockState state, FluidState fluidState) {
            if (state.is(Blocks.WATER)) {
                return Optional.empty(); // mmm water
            }
            return state.is(Blocks.NETHER_PORTAL) ? Optional.empty() : super.getBlockExplosionResistance(explosion, level, pos, state, fluidState);
        }
    };

    private @Nullable EntityReference<LivingEntity> owner;
    private float explosionPower;

    public PrimedPlumbersTnt(EntityType<? extends PrimedPlumbersTnt> type, Level level) {
        super(type, level);
        explosionPower = 4.0f;
    }

    public static PrimedPlumbersTnt create(final Level level, final double x, final double y, final double z, @Nullable final LivingEntity optionalOwner) {
        PrimedPlumbersTnt primed = new PrimedPlumbersTnt(ExtraEntities.PRIMED_PLUMBERS_TNT.get(), level);
        primed.setPos(x, y, z);
        double d0 = level.getRandom().nextDouble() * (double)((float)Math.PI * 2F);
        primed.setDeltaMovement(-Math.sin(d0) * 0.02, 0.2, -Math.cos(d0) * 0.02);
        primed.setFuse(80);
        primed.xo = x;
        primed.yo = y;
        primed.zo = z;
        primed.owner = EntityReference.of(optionalOwner);
        return primed;
    }

    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_FUSE_ID, 80);
        builder.define(DATA_BLOCK_STATE_ID, DEFAULT_BLOCK_STATE);
    }

    @Override
    public void setBlockState(BlockState blockState) {
        entityData.set(DATA_BLOCK_STATE_ID, blockState);
    }

    @Override
    public BlockState getBlockState() {
        return entityData.get(DATA_BLOCK_STATE_ID);
    }

    public void setFuse(int life) {
        entityData.set(DATA_FUSE_ID, life);
    }

    public int getFuse() {
        return entityData.get(DATA_FUSE_ID);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        setFuse(input.getShortOr("fuse", (short) 80));
        explosionPower = input.getFloatOr("ExplosionPower", 0.0f);
        setBlockState(input.read("block_state", BlockState.CODEC).orElse(DEFAULT_BLOCK_STATE));
        this.owner = EntityReference.read(input, "owner");
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putShort("fuse", (short) getFuse());
        output.putFloat("ExplosionPower", explosionPower);
        output.store("block_state", BlockState.CODEC, getBlockState());
        EntityReference.store(this.owner, output, "owner");
    }

    protected void explode() {
        if (level() instanceof ServerLevel serverlevel) {
            serverlevel.explode(this, Explosion.getDefaultDamageSource(serverlevel, this), DAMAGE_CALCULATOR, getX(), getY(0.0625F), getZ(), explosionPower, false, Level.ExplosionInteraction.TNT);
        }
    }
}
