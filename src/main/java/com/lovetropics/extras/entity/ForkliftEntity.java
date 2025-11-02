package com.lovetropics.extras.entity;

import com.lovetropics.extras.ExtraItems;
import com.lovetropics.extras.ExtraLangKeys;
import com.lovetropics.extras.client.keybinds.ForkliftKeybinds;
import com.lovetropics.extras.network.message.ServerboundDriftForkliftPacket;
import com.lovetropics.extras.network.message.ServerboundLiftForkliftPacket;
import com.mojang.serialization.Codec;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.game.ServerboundMoveVehiclePacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
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
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.InterpolationHandler;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PlayerRideable;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class ForkliftEntity extends Entity implements PlayerRideable {
    private static final EntityDataAccessor<Integer> DATA_FORK_HEIGHT = SynchedEntityData.defineId(ForkliftEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_IS_DRIFTING = SynchedEntityData.defineId(ForkliftEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Optional<BlockPredicate>> DATA_COLLISION_PREDICATE = SynchedEntityData.defineId(ForkliftEntity.class, ExtraSerializers.BLOCK_PREDICATE.get());

    private static final Component CERTIFICATION_MISSING = ExtraLangKeys.FORKLIFT_CERTIFICATION_MISSING.get().withStyle(ChatFormatting.RED);

    private static final int MAX_PASSENGERS = 3;
    public static final int MIN_FORK_HEIGHT = 0;
    public static final int MAX_FORK_HEIGHT = 18;
    private static final float RIDER_X_OFFSET = 0.8f;
    private static final float RIDER_Z_OFFSET = 2.75f;
    public static final float FORKLIFT_SCALE = 2.4f;
    public static final double FRICTION = 0.85f;
    public static final double DRIFT_FRICTION = 0.9f;
    public static final int DRIFT_TICKS = 50;

    public int driftBuildTicks = 0;
    public int driftDuration = 0;
    public int driftCooldown = 0;
    public float driftStrength = 0.0f;

    public float lastForkHeight;
    public float wheelRot;
    public float lastWheelRot;

    private final InterpolationHandler interpolation = new InterpolationHandler(this, 3);

    private boolean requiresCertification;

    public ForkliftEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity passenger) {
        if (passenger.is(getControllingPassenger())) {
            return super.getDismountLocationForPassenger(passenger);
        }
        return getPickupAABB().getCenter().add(0, (20 - this.getForkHeight()) / 16f * FORKLIFT_SCALE, 0);
    }

    public AABB getPickupAABB() {
        Vec3 lookVec = getLookAngle();
        return getBoundingBox().move(lookVec.normalize().multiply(2, 2, 2));
    }

    private void pickupEntitiesInFront() {
        if (!level().isClientSide && hasControllingPassenger() && getPassengers().size() < MAX_PASSENGERS && getKnownMovement().lengthSqr() > 0) {
            Predicate<Entity> predicate = EntitySelector.NO_SPECTATORS.and(this::canCollideWith).and(p -> p != getControllingPassenger() && !p.isPassenger());
            List<Entity> list = level().getEntities(this, getPickupAABB(), predicate);
            if (!list.isEmpty()) {
                for (Entity e : list) {
                    // Hurt anything when picked up not near the bottom
                    if (getForkHeight() < MAX_FORK_HEIGHT - 3) {
                        e.hurtServer((ServerLevel) level(), damageSources().thorns(this), 1);
                    }
                    e.startRiding(this);
                }
            }
        }
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
        final float forkRiderOffset = 2.8f - forkHeight;
        final float riderYRot = -getYRot() * ((float) Math.PI / 180F);

        if (riderIndex == 1) {
            return new Vec3(-RIDER_X_OFFSET, forkRiderOffset, RIDER_Z_OFFSET).yRot(riderYRot);
        } else if (riderIndex == 2) {
            return new Vec3(RIDER_X_OFFSET, forkRiderOffset, RIDER_Z_OFFSET).yRot(riderYRot);
        }

        return super.getPassengerAttachmentPoint(entity, dimensions, partialTick);
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
        builder.define(DATA_COLLISION_PREDICATE, Optional.empty());
    }

    private void eject() {
        ClientPacketDistributor.sendToServer(new ServerboundLiftForkliftPacket(true, MIN_FORK_HEIGHT, getId()));
    }

    private void sendForkHeightFromClient(final int height) {
        ClientPacketDistributor.sendToServer(new ServerboundLiftForkliftPacket(false, height, getId()));
    }

    public void setForkHeight(final int height) {
        entityData.set(DATA_FORK_HEIGHT, Mth.clamp(height, MIN_FORK_HEIGHT, MAX_FORK_HEIGHT));
    }

    public int getForkHeight() {
        return entityData.get(DATA_FORK_HEIGHT);
    }

    public float getForkHeight(float partialTick) {
        return partialTick == 1.0F ? this.getForkHeight() : Mth.lerp(partialTick, this.lastForkHeight, this.getForkHeight());
    }

    public float getWheelRot(float partialTick) {
        return partialTick == 1.0F ? this.wheelRot : Mth.lerp(partialTick, this.lastWheelRot, this.wheelRot);
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
        entityData.set(DATA_COLLISION_PREDICATE, input.read("CollisionPredicate", BlockPredicate.CODEC));
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        output.putBoolean("RequiresCertification", requiresCertification);
        output.putInt("DriftDuration", driftDuration);
        output.storeNullable("CollisionPredicate", BlockPredicate.CODEC, entityData.get(DATA_COLLISION_PREDICATE).orElse(null));
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
        this.lastForkHeight = this.getForkHeight();
        this.tickWheelRotation();

        if (isLocalInstanceAuthoritative()) {
            applyGravity();

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
        }
        else {
            setDeltaMovement(Vec3.ZERO);
        }

        move(MoverType.SELF, getDeltaMovement());

        pickupEntitiesInFront();
    }


    public void tickWheelRotation() {
        this.lastWheelRot = this.wheelRot;

        float yRotRad = this.getYRot() * ((float)Math.PI / 180F);
        float forwardX = -Mth.sin(yRotRad);
        float forwardZ = Mth.cos(yRotRad);
        float signedSpeed = (float)(this.getDeltaMovement().x * forwardX + this.getDeltaMovement().z * forwardZ);
        this.wheelRot += Mth.clamp(signedSpeed, -360f, 360f);
    }

    @Override
    protected double getDefaultGravity() {
        return 0.04;
    }

    private void applyFriction(double friction) {
        Vec3 velocity = getDeltaMovement();
        setDeltaMovement(velocity.x * friction, velocity.y, velocity.z * friction);
    }

    private void moveFork(int amt) {
        sendForkHeightFromClient(getForkHeight() + amt);
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
            boolean eject_riders = ForkliftKeybinds.EJECT_FORK_RIDERS.isDown();

            // Must have enough space to eject
            if (eject_riders && getForkHeight() > MIN_FORK_HEIGHT) {
                eject();
            }

            if (liftUp) {
                moveFork(-1);
            }

            if (liftDown) {
                moveFork(1);
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

    public void destroyBlock(BlockState state, Level level, BlockPos pos) {
        level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.EXPLOSION.getType(), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 1, 0.5, 0.5, 0.5, 0.1);
        }
    }

    public boolean shouldDestroyBlock(BlockPos pos, BlockState state) {
        return entityData.get(DATA_COLLISION_PREDICATE)
                .map(blockPredicate -> blockPredicate
                        .matches(new BlockInWorld(level(), pos, false)))
                .orElse(false);
    }


    @Override
    public final ItemStack getPickResult() {
        return new ItemStack(ExtraItems.FORKLIFT_SPAWN_EGG.get());
    }
}
