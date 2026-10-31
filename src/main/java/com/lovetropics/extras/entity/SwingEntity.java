package com.lovetropics.extras.entity;

import com.lovetropics.extras.ExtraItems;
import com.lovetropics.extras.network.message.ServerboundSwingPacket;
import com.mojang.serialization.Codec;
import net.minecraft.client.player.LocalPlayer;
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
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.jspecify.annotations.Nullable;

public class SwingEntity extends Entity implements PlayerRideable {

    private static final EntityDataAccessor<Float> DATA_SWING_HEIGHT = SynchedEntityData.defineId(SwingEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> DATA_PLAYER_INPUT = SynchedEntityData.defineId(SwingEntity.class, EntityDataSerializers.BOOLEAN);

    private static final float MAX_ANGLE = (float) Math.toRadians(75);
    private static final float GRAVITY = 9.81f / 2.5f;

    private float velocity = 0f;

    public float prevClientSwingHeight = 0f;
    public float clientSwingHeight = 0f;

    public SwingEntity(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand, Vec3 location) {
        if (!level().isClientSide() && !player.isShiftKeyDown()) {
            player.startRiding(this);
            return InteractionResult.SUCCESS;
        }

        return !player.isPassengerOfSameVehicle(this) ? InteractionResult.SUCCESS : InteractionResult.PASS;
    }


    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_SWING_HEIGHT, 0f);
        builder.define(DATA_PLAYER_INPUT, false);
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
        entityData.set(DATA_SWING_HEIGHT, input.read("SwingHeight", Codec.FLOAT).orElse(0f));
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        output.putFloat("SwingHeight", entityData.get(DATA_SWING_HEIGHT));
    }

    public float getSwingHeight() {
        return entityData.get(DATA_SWING_HEIGHT);
    }

    public float getVelocity() {
        return velocity;
    }

    public float getAngle() {
        return getSwingHeight() * MAX_ANGLE;
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    public void setPlayerInput(boolean playerInput) {
        entityData.set(DATA_PLAYER_INPUT, playerInput);
    }

    @Override
    public @Nullable LivingEntity getControllingPassenger() {
        Entity firstPassenger = getFirstPassenger();
        LivingEntity controllingPassenger;
        if (firstPassenger instanceof LivingEntity passenger) {
            controllingPassenger = passenger;
        } else {
            controllingPassenger = null;
        }

        return controllingPassenger;
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return getPassengers().isEmpty();
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide()) {
            prevClientSwingHeight = clientSwingHeight;
            clientSwingHeight = getAngle();
            controlSwing();
            return;
        }
        boolean isPushing = entityData.get(DATA_PLAYER_INPUT);
        if(getPassengers().isEmpty()) {
            entityData.set(DATA_PLAYER_INPUT, false);
            isPushing = false;
        }
        float swingHeight = getSwingHeight();
        float accel = -GRAVITY * (float) Math.sin(getAngle()) / MAX_ANGLE
                - 0.25f * getVelocity();

        if(isPushing && velocity >= -0.05f) {
            accel += 1.5f;
        }
        velocity += accel * 0.05f;
        swingHeight += velocity * 0.05f;

        if(swingHeight > 1f) {
            swingHeight = 1f;
            velocity = Math.min(velocity, 0f);
        }
        if(swingHeight < -1f) {
            swingHeight = -1f;
            velocity = Math.max(velocity, 0f);
        }
        if (!isPushing && Math.abs(swingHeight) < 0.01f && Math.abs(velocity) < 0.02f) {
            swingHeight = 0f;
            velocity = 0f;
        }
        entityData.set(DATA_SWING_HEIGHT, swingHeight);
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return entity.isPushable() || super.canCollideWith(entity) || entity instanceof FallingBlockEntity || entity instanceof PrimedTnt;
    }

    @Override
    public boolean canBeCollidedWith(@Nullable Entity entity) {
        return true;
    }

    private void sendSwingFromClient(boolean inputForward) {
        ClientPacketDistributor.sendToServer(new ServerboundSwingPacket(inputForward, getId()));
    }

    private void controlSwing() {
        if (this.isVehicle() && getControllingPassenger() instanceof final LocalPlayer localPlayer) {
            boolean inputForward = localPlayer.input.keyPresses.forward();
            if(entityData.get(DATA_PLAYER_INPUT) != inputForward){
                sendSwingFromClient(inputForward);
            }
        }
    }

    private Vec3 getSeatPosition(float angle){
        float yaw = (float) Math.toRadians(getYRot());

        double forwardX = -Math.sin(yaw);
        double forwardZ = Math.cos(yaw);

        double out = 2.5f * Mth.sin(angle);
        double down = 2.5f * Mth.cos(angle);

        return new Vec3(getX() + forwardX * out, getY() + 3f - down, getZ() + forwardZ * out);
    }

    public float getAngleDegrees(float partialTick) {
        float h = Mth.lerp(partialTick, prevClientSwingHeight, clientSwingHeight);
        return h * MAX_ANGLE;
    }

    @Override
    protected void positionRider(Entity passenger, MoveFunction moveFunction) {
        if(!hasPassenger(passenger)) return;
        Vec3 seatPosition = getSeatPosition(getAngle());
        Vec3 riderOffset = passenger.getVehicleAttachmentPoint(this);
        moveFunction.accept(passenger, seatPosition.x - riderOffset.x,
                seatPosition.y - 1f, seatPosition.z - riderOffset.z);
        clampPassengerRotation(passenger);
    }

    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity passenger) {
        Vec3 launchVelocity = getLaunchVelocity();
        if(launchVelocity.length() * 20f > 1f){
            passenger.setDeltaMovement(launchVelocity);
            passenger.hurtMarked = true;
            return null;
        }
        boolean isSwingingForward = getSwingHeight() > 0.1f;
        float yaw = this.getYRot() * Mth.DEG_TO_RAD;
        Vec3 vec3 = new Vec3(-Mth.sin(yaw), 0, Mth.cos(yaw));
        return position().add(vec3.scale(2.5f * (isSwingingForward ? 1 : -1)));
    }

    @Override
    public void onPassengerTurned(Entity passenger) {
        super.onPassengerTurned(passenger);
        clampPassengerRotation(passenger);
    }

    @Override
    protected Vec3 getPassengerAttachmentPoint(Entity passenger, EntityDimensions dimensions, float scale) {
        return new Vec3(0f, 0.3f, 0);
    }

    @Override
    protected void addPassenger(Entity passenger) {
        super.addPassenger(passenger);
        passenger.setYRot(getYRot());
        passenger.setYHeadRot(getYRot());
        if(passenger instanceof LivingEntity livingEntity) {
            livingEntity.setYBodyRot(getYRot());
        }
    }

    private Vec3 getLaunchVelocity(){
        float angle = getAngle();
        float angleSpeed = velocity * MAX_ANGLE;
        float speed = 2.5f * angleSpeed;

        float yaw = (float) Math.toRadians(getYRot());
        double forwardX = -Math.sin(yaw);
        double forwardZ = Math.cos(yaw);

        float hoz = speed * Mth.cos(angle);
        float vert = speed * Mth.sin(angle);

        return new Vec3(forwardX * hoz, vert, forwardZ * hoz)
                .scale(3f / 20);
    }

    private void clampPassengerRotation(Entity passenger){
        passenger.setYBodyRot(getYRot());
        float diff = Mth.wrapDegrees(passenger.getYRot() - getYRot());
        float clamped = Mth.clamp(diff, -105f, 105f);
        passenger.yRotO += clamped - diff;
        passenger.setYRot(passenger.getYRot() + clamped -diff);
        passenger.setYHeadRot(passenger.getYRot());
    }
}
