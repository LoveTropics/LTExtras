package com.lovetropics.extras.block.entity;

import com.lovetropics.extras.ExtraItems;
import com.lovetropics.extras.ExtraLangKeys;
import com.lovetropics.extras.client.keybinds.ForkliftKeybinds;
import com.lovetropics.extras.network.message.ServerboundDriftForkliftPacket;
import com.lovetropics.extras.network.message.ServerboundLiftForkliftPacket;
import com.mojang.serialization.Codec;
import net.minecraft.ChatFormatting;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundMoveVehiclePacket;
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
import net.minecraft.world.entity.InterpolationHandler;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PlayerRideable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import javax.annotation.Nullable;

public class ForkliftEntity extends Entity implements PlayerRideable {
    private static final EntityDataAccessor<Integer> DATA_FORK_HEIGHT = SynchedEntityData.defineId(ForkliftEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_IS_DRIFTING = SynchedEntityData.defineId(ForkliftEntity.class, EntityDataSerializers.BOOLEAN);

    private static final Component CERTIFICATION_MISSING = ExtraLangKeys.FORKLIFT_CERTIFICATION_MISSING.get().withStyle(ChatFormatting.RED);

    private static final int MAX_PASSENGERS = 3;
    private static final int MIN_FORK_HEIGHT = -5;
    private static final int MAX_FORK_HEIGHT = 20;
    private static final float RIDER_X_OFFSET = 0.3f;
    private static final float RIDER_Z_OFFSET = 2.0f;
    public static final float FORKLIFT_SCALE = 1.2f;
    public static final double FRICTION = 0.85f;
    public static final double DRIFT_FRICTION = 0.9f;
    public static final int DRIFT_TICKS = 50;

    public int driftBuildTicks = 0;
    public int driftDuration = 0;
    public int driftCooldown = 0;
    public float driftStrength = 0.0f;

    private final InterpolationHandler interpolation = new InterpolationHandler(this, 3);

    private boolean requiresCertification;

    public ForkliftEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    private boolean hasItem(final Player player, final Item item) {
        return player.getInventory().contains(p -> p.is(item));
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (!level().isClientSide && !player.isShiftKeyDown()) {
            if (!requiresCertification || hasItem(player, ExtraItems.FORKLIFT_CERTIFICATION.asItem())) {
                player.startRiding(this);
                return InteractionResult.SUCCESS;
            } else {
                if (player instanceof final ServerPlayer serverPlayer) {
                    serverPlayer.sendSystemMessage(CERTIFICATION_MISSING, true);
                }
            }
        }

        return !player.isPassengerOfSameVehicle(this) ? InteractionResult.SUCCESS : InteractionResult.PASS;
    }

    @Override
    protected Vec3 getPassengerAttachmentPoint(Entity entity, EntityDimensions dimensions, float partialTick) {
        final int riderIndex = getPassengers().indexOf(entity);
        final float forkHeight = FORKLIFT_SCALE * getForkHeight() / 16.0f;
        final float forkRiderOffset = 1.8f - forkHeight;
        final float riderYRot = -getYRot() * ((float) Math.PI / 180F);

        if (riderIndex == 1) {
            return new Vec3(-RIDER_X_OFFSET, forkRiderOffset, RIDER_Z_OFFSET).yRot(riderYRot);
        } else if (riderIndex == 2) {
            return new Vec3(RIDER_X_OFFSET, forkRiderOffset, RIDER_Z_OFFSET).yRot(riderYRot);
        }

        return super.getPassengerAttachmentPoint(entity, dimensions, partialTick);
    }

    @Override
    public boolean canBeCollidedWith(@Nullable Entity entity) {
        return true;
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource damageSource, float amount) {
        return false;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_FORK_HEIGHT, 0);
        builder.define(DATA_IS_DRIFTING, false);
    }

    private void setForkHeightFromClient(final int height) {
        ClientPacketDistributor.sendToServer(new ServerboundLiftForkliftPacket(height, getId()));
    }

    public void setForkHeight(final int height) {
        entityData.set(DATA_FORK_HEIGHT, Mth.clamp(height, MIN_FORK_HEIGHT, MAX_FORK_HEIGHT));
    }

    public int getForkHeight() {
        return entityData.get(DATA_FORK_HEIGHT);
    }

    public void setDrifting(final boolean drifting) {
        entityData.set(DATA_IS_DRIFTING, drifting);
    }

    public boolean isDrifting() {
        return entityData.get(DATA_IS_DRIFTING);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        requiresCertification = input.read("RequiresCertification", Codec.BOOL).orElse(false);
        driftDuration = input.read("DriftDuration", Codec.INT).orElse(0);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        output.putBoolean("RequiresCertification", requiresCertification);
        output.putInt("DriftDuration", driftDuration);
    }

    @Override
    @Nullable
    public LivingEntity getControllingPassenger() {
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
        return getPassengers().size() < MAX_PASSENGERS;
    }

    @Override
    @Nullable
    public InterpolationHandler getInterpolation() {
        return interpolation;
    }

    @Override
    public void tick() {
        super.tick();

        interpolation.interpolate();

        if (isLocalInstanceAuthoritative()) {
            if (level().isClientSide) {
                if (isDrifting() && driftDuration == 0) {
                    ClientPacketDistributor.sendToServer(new ServerboundDriftForkliftPacket(false, getId()));
                    driftCooldown = DRIFT_TICKS;
                }

                if (driftDuration > 0) {
                    driftDuration--;
                }

                if (driftCooldown > 0) {
                    driftCooldown--;
                }

                controlForklift();
            }

            // TODO add gravity
            //setDeltaMovement(getDeltaMovement().x, getDeltaMovement().y - getDefaultGravity(), getDeltaMovement().z);
        }
        else {
            setDeltaMovement(Vec3.ZERO);
        }

        move(MoverType.SELF, getDeltaMovement());
    }

    @Override
    protected double getDefaultGravity() {
        return 0.04;
    }

    private void applyFriction(double friction) {
        Vec3 velocity = getDeltaMovement();
        setDeltaMovement(velocity.x * friction, velocity.y - getDefaultGravity(), velocity.z * friction);
    }

    private void moveFork(int amt) {
        setForkHeightFromClient(getForkHeight() + amt);
    }

    private void controlForklift() {
        if (this.isVehicle() && getControllingPassenger() instanceof final LocalPlayer localPlayer) {
            float f = 0.0F;
            boolean inputLeft = localPlayer.input.keyPresses.left();
            boolean inputRight = localPlayer.input.keyPresses.right();
            boolean inputUp = localPlayer.input.keyPresses.forward();
            boolean inputDown = localPlayer.input.keyPresses.backward();

            boolean liftUp = ForkliftKeybinds.RAISE_FORKLIFT.isDown();
            boolean liftDown = ForkliftKeybinds.LOWER_FORKLIFT.isDown();
            boolean drift = ForkliftKeybinds.DRIFT.isDown();

            if (liftUp) {
                moveFork(1);
            }

            if (liftDown) {
                moveFork(-1);
            }

            if (inputLeft) {
                setYRot(getYRot() - 5f);
            }

            if (inputRight) {
                setYRot(getYRot() + 5f);
            }

            if (inputRight != inputLeft && !inputUp && !inputDown) {
                f += 0.005F;
            }

            if (inputUp) {
                f += 0.05F;
            }

            if (inputDown) {
                f -= 0.05F;
            }

            final boolean buildingDrift = driftCooldown == 0 && !isDrifting() && drift && ((inputUp && inputRight) || (inputDown && inputRight) || (inputUp && inputLeft) || (inputDown && inputLeft));
            if (buildingDrift) {
                driftBuildTicks++;
            } else {
                if (driftBuildTicks > 0 && !isDrifting() && driftCooldown == 0) {
                    ClientPacketDistributor.sendToServer(new ServerboundDriftForkliftPacket(true, getId()));
                    driftDuration = DRIFT_TICKS;
                    driftStrength = driftBuildTicks / (float) DRIFT_TICKS;
                }

                driftBuildTicks = 0;
            }

            driftBuildTicks = Mth.clamp(driftBuildTicks, 0, DRIFT_TICKS);

            if (isDrifting()) {
                executeDrift(getDeltaMovement());
            } else {
                applyFriction(FRICTION);
                setDeltaMovement(getDeltaMovement().add(Mth.sin(-this.getYRot() * ((float)Math.PI / 180F)) * f, 0.0F, Mth.cos(this.getYRot() * ((float)Math.PI / 180F)) * f));
            }

            if (isPassenger()) {
                localPlayer.connection.send(ServerboundMoveVehiclePacket.fromEntity(this));
            }
        }
    }

    private void executeDrift(Vec3 travelVector) {
        hasImpulse = true;

        if (travelVector.lengthSqr() > Mth.EPSILON) {
            float xVelocity = Mth.sin(this.getYRot() * ((float)Math.PI / 180F));
            float zVelocity = Mth.cos(this.getYRot() * ((float)Math.PI / 180F));
            float boost = driftStrength / 10.f;
            setDeltaMovement(getDeltaMovement().add(-xVelocity * boost * DRIFT_FRICTION, 0.0F, zVelocity * boost * DRIFT_FRICTION));
        }
    }
}
