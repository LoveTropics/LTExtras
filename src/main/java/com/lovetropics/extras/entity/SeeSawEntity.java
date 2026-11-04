package com.lovetropics.extras.entity;

import com.mojang.serialization.Codec;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PlayerRideable;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class SeeSawEntity extends BlockFormationEntity implements PlayerRideable {

    private static final EntityDataAccessor<Float> DATA_LEVEL = SynchedEntityData.defineId(SeeSawEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_ARM_LENGTH = SynchedEntityData.defineId(SeeSawEntity.class, EntityDataSerializers.FLOAT);

    private static final float MAX_ANGLE = 20f;

    public float prevClientLevel = 0f;
    public float clientLevel = 0f;
    private float velocity = 0f;

    public SeeSawEntity(EntityType<?> type, Level level) {
        super(type, level);
    }

    public static SeeSawEntity create(Level level, BlockFormation formation) {
        SeeSawEntity seeSawEntity = new SeeSawEntity(ExtraEntities.SEE_SAW.get(), level);
        seeSawEntity.setBlockFormation(formation);
        return seeSawEntity;
    }

    @Override
    public void setBlockFormation(BlockFormation blockFormation) {
        super.setBlockFormation(blockFormation);
        if(blockFormation.getSeats().isEmpty()) return;
        Vec3 pivot = Vec3.atCenterOf(getBlockFormation().getAnchorBlock());
        Vec3 seat = Vec3.atCenterOf(getBlockFormation().getSeats().get(0).offset(getBlockFormation().getAnchorBlock()));
        entityData.set(DATA_ARM_LENGTH, (float) pivot.distanceTo(seat));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_LEVEL, 0f);
        builder.define(DATA_ARM_LENGTH, 0f);
    }

    @Override
    public void setPos(double x, double y, double z) {
        super.setPos(x, y, z);
        if(getBlockFormation() != null) {
            setYRot(Direction.getApproximateNearest(Vec3.atLowerCornerOf(getBlockFormation().getSeats().get(0))).toYRot());
        }
    }

    @Override
    public boolean hurtServer(ServerLevel serverLevel, DamageSource damageSource, float v) {
        if (this.isRemoved()) {
            return true;
        }

        if(damageSource.getEntity() instanceof Player player && player.getAbilities().instabuild) {
            this.markHurt();
            this.gameEvent(GameEvent.ENTITY_DAMAGE, damageSource.getEntity());
            this.discard();
            return true;
        }

        return false;
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        entityData.set(DATA_LEVEL, input.read("Level", Codec.FLOAT).orElse(0f));
        entityData.set(DATA_ARM_LENGTH, input.read("ArmLength", Codec.FLOAT).orElse(0f));
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putFloat("Level", entityData.get(DATA_LEVEL));
        output.putFloat("ArmLength", entityData.get(DATA_ARM_LENGTH));
    }

    public float getLevel() {
        return entityData.get(DATA_LEVEL);
    }
    public float getArmLength() {
        return entityData.get(DATA_ARM_LENGTH);
    }

    public float getAngle() {
        return getLevel() * MAX_ANGLE;
    }
    public float getAngleDegrees(float partialTick) {
        float h = Mth.lerp(partialTick, prevClientLevel, clientLevel);
        return h * MAX_ANGLE;
    }
    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide()) {
            prevClientLevel = clientLevel;
            clientLevel = getAngle();
            return;
        }
        float seeSawLevel = getLevel();
        float frontWeight = hasPlayer(0) ? 1f : 0f;
        float backWeight = hasPlayer(1) ? 1f : 0f;
        float weight = frontWeight + backWeight + 0.5f;

        // Figure out who's pushing?
        if(isPushing(0) && frontWeight > 0f && seeSawLevel >= -1f) {
            velocity = 0.5f * (1.5f / weight);
        }
        if(isPushing(1) && backWeight > 0f && seeSawLevel <= 1f) {
            velocity = -0.5f * (1.5f / weight);
        }

        float angle = getAngle();
        float accel = 9.81f * (frontWeight - backWeight) * Mth.cos(angle)
                / (weight * getArmLength()) / MAX_ANGLE;

        accel -= 0.5f * velocity;

        velocity += accel * 0.05f;
        seeSawLevel += velocity * 0.05f;

        if(seeSawLevel >= 1){
            seeSawLevel = 1f;
            velocity = Math.min(velocity, 0f);
        } else if(seeSawLevel <= -1){
            seeSawLevel = -1f;
            velocity = Math.max(velocity, 0f);
        }
        entityData.set(DATA_LEVEL, seeSawLevel);
    }

    private boolean hasPlayer(int index){
        return getPassengers().size() > index;
    }

    private boolean isPushing(int index){
        return hasPlayer(index) && getPassengers().get(index) instanceof ServerPlayer sp && sp.getLastClientInput().jump();
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return entity.isPushable() || super.canCollideWith(entity) || entity instanceof FallingBlockEntity || entity instanceof PrimedTnt;
    }

    @Override
    public boolean canBeCollidedWith(@Nullable Entity entity) {
        return true;
    }


    @Override
    protected void positionRider(Entity passenger, MoveFunction moveFunction) {
        int seat = getPassengers().indexOf(passenger);
        if (seat < 0) return;
        boolean isFront = seat == 0;
        float dir = isFront ? -1 : 1;
        float angle = getAngle() * Mth.DEG_TO_RAD;
        float along = getArmLength() * Mth.cos(angle) * dir;
        float up = getArmLength() * Mth.sin(angle) * dir;

        boolean facingZ = Direction.fromYRot(getYRot()).getAxis() == Direction.Axis.Z;
        double dx, dz;
        if(facingZ){
            dx = 0;
            dz = along;
        } else {
            dx = along;
            dz = 0;
            up = -up;
        }

        Vec3 offset = this.getPassengerAttachmentPoint(passenger, this.getDimensions(passenger.getPose()), 1.0F);

        moveFunction.accept(passenger,
                getX() + dx + 0.5f,
                getY() + offset.y + up,
                getZ() + dz + 0.5f);
    }

    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity passenger) {
        return position();
    }

}
