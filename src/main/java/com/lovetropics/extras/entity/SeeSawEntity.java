package com.lovetropics.extras.entity;

import com.lovetropics.extras.network.message.ServerboundSwingPacket;
import com.mojang.serialization.Codec;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PlayerRideable;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.jspecify.annotations.Nullable;

import java.util.Map;

public class SeeSawEntity extends BlockFormationEntity implements PlayerRideable {

    private static final EntityDataAccessor<Float> DATA_LEVEL = SynchedEntityData.defineId(SeeSawEntity.class, EntityDataSerializers.FLOAT);

    private static final float MAX_ANGLE = 75f;

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
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_LEVEL, 0f);
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
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putFloat("Level", entityData.get(DATA_LEVEL));
    }

    public float getLevel() {
        return entityData.get(DATA_LEVEL);
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
        float weight = frontWeight + backWeight + 0.2f;

        // Figure out who's pushing?
        if(isPushing(0) && frontWeight > 0f && seeSawLevel <= -1f) {
            velocity = -4f * (1.5f / weight);
        }
        if(isPushing(1) && backWeight > 0f && seeSawLevel >= 1f) {
            velocity = 4f * (1.5f / weight);
        }

        float angle = getAngle();
        float accel = 9.81f * (frontWeight - backWeight) * Mth.cos(angle)
                / (weight * 1.5f) / MAX_ANGLE;

        accel -= 0.5f * velocity;

        velocity += accel * 0.05f;
        seeSawLevel += velocity * 0.05f;

        boolean hitGround = false;
        if(seeSawLevel >= 1){
            hitGround = velocity > 0.5f;
            seeSawLevel = 1f;
            velocity = Math.min(velocity, 0f);
        } else if(seeSawLevel <= -1){
            hitGround = velocity < -0.5f;
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


    private Vec3 getSeatPosition(int passengerIndex, float angle){
        float yaw = (float) Math.toRadians(getYRot());

        double forwardX = -Math.sin(yaw);
        double forwardZ = Math.cos(yaw);

        double out = 2.5f * Mth.sin(angle);
        double down = 2.5f * Mth.cos(angle);

        return new Vec3(getX() + forwardX * out, getY() + 3f - down, getZ() + forwardZ * out);
    }

    @Override
    protected void positionRider(Entity passenger, MoveFunction moveFunction) {
//        int seat = getPassengers().indexOf(passenger);
//        if (seat < 0) return;
//        boolean isFront = seat == 0;
//        float angle = getAngle();
//        float forward = 1.5f * Mth.cos(angle) * (isFront ? 1 : -1);
//        float height  = 1.5f * Mth.sin(angle) * (isFront ? -1 : 1);
//
//        float yaw = getYRot() * Mth.DEG_TO_RAD;
//        Vec3 offset = passenger.getVehicleAttachmentPoint(this);
//        moveFunction.accept(passenger,
//                getX() - Mth.sin(yaw) * forward - offset.x,
//                getY() + 0.6f + height - offset.y,
//                getZ() + Mth.cos(yaw) * forward - offset.z);
        super.positionRider(passenger, moveFunction);
    }

    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity passenger) {
        return position();
    }

    private Vec3 getLaunchVelocity(){
        return new Vec3(0, 0.5, 0);
    }
}
