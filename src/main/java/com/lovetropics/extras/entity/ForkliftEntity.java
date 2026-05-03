package com.lovetropics.extras.entity;

import com.lovetropics.extras.ExtraItems;
import com.lovetropics.extras.ExtraLangKeys;
import com.lovetropics.extras.client.keybinds.ForkliftKeybinds;
import com.lovetropics.extras.client.particle.ExtraParticles;
import com.lovetropics.extras.network.message.ServerboundDriftForkliftPacket;
import com.lovetropics.extras.network.message.ServerboundLiftForkliftPacket;
import com.lovetropics.extras.sounds.ExtraSounds;
import com.mojang.serialization.Codec;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.advancements.criterion.BlockPredicate;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundMoveVehiclePacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.CommonColors;
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
import net.minecraft.world.level.gameevent.GameEvent;
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
    private static final EntityDataAccessor<Integer> SPEED_BOOST_TICKS = SynchedEntityData.defineId(ForkliftEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> SPEED_BOOST_STRENGTH = SynchedEntityData.defineId(ForkliftEntity.class, EntityDataSerializers.FLOAT);

    private static final EntityDataAccessor<Optional<BlockPredicate>> DATA_COLLISION_PREDICATE = SynchedEntityData.defineId(ForkliftEntity.class, ExtraSerializers.BLOCK_PREDICATE.get());
    private static final EntityDataAccessor<Optional<BlockPredicate>> DATA_SLOW_PREDICATE = SynchedEntityData.defineId(ForkliftEntity.class, ExtraSerializers.BLOCK_PREDICATE.get());
    private static final EntityDataAccessor<Float> DATA_SLOW_MULTIPLIER = SynchedEntityData.defineId(ForkliftEntity.class, EntityDataSerializers.FLOAT);

    private static final Component CERTIFICATION_MISSING = ExtraLangKeys.FORKLIFT_CERTIFICATION_MISSING.get().withStyle(ChatFormatting.RED);

    private static final float DEFAULT_SPEED_BOOST_STRENGTH = 0.05f;
    private static final int MAX_PASSENGERS = 3;
    public static final int MIN_FORK_HEIGHT = 0;
    public static final int MAX_FORK_HEIGHT = 18;
    public static final int FORK_HEIGHT = MAX_FORK_HEIGHT - MIN_FORK_HEIGHT;
    private static final float RIDER_X_OFFSET = 0.8f;
    private static final float RIDER_Z_OFFSET = 2.75f;
    public static final float FORKLIFT_SCALE = 2.4f;
    public static final double FRICTION = 0.85f;
    public static final double DRIFT_FRICTION = 0.9f;
    public static final int DRIFT_TICKS = 50;

    private static final boolean PICKUP_DEBUG = false;

    public int driftBuildTicks = 0;
    public int driftDuration = 0;
    public int driftCooldown = 0;
    public float driftStrength = 0.0f;
    private float deltaRotation;

    public int renderForkHeight;
    public int renderForkHeight0;
    public float wheelRot;
    public float lastWheelRot;
    public boolean shouldPickupEntities = true;

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
        return getPickupAABB().getCenter().add(0, (2 + FORK_HEIGHT - this.getForkHeight()) / 16f * FORKLIFT_SCALE, 0);
    }

    public AABB getPickupAABB() {
        Vec3 lookVec = getLookAngle();
        return getBoundingBox().move(lookVec.normalize().multiply(2, 2, 2));
    }

    private void pickupEntitiesInFront() {
        if (!level().isClientSide() && hasControllingPassenger() && getPassengers().size() < MAX_PASSENGERS && getKnownMovement().lengthSqr() > 0) {
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
    public InteractionResult interact(Player player, InteractionHand hand, Vec3 location) {
        if (!level().isClientSide() && !player.isShiftKeyDown()) {
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
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_FORK_HEIGHT, MAX_FORK_HEIGHT);
        builder.define(DATA_IS_DRIFTING, false);
        builder.define(DATA_COLLISION_PREDICATE, Optional.empty());
        builder.define(SPEED_BOOST_TICKS, 0);
        builder.define(SPEED_BOOST_STRENGTH, DEFAULT_SPEED_BOOST_STRENGTH);
        builder.define(DATA_SLOW_PREDICATE, Optional.empty());
        builder.define(DATA_SLOW_MULTIPLIER, 0.5f);
    }

    private void tryEject() {
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

    public float getRenderForkHeight(float partialTick) {
        return partialTick == 1.0F ? this.getForkHeight() : Mth.lerp(partialTick, this.renderForkHeight0, this.renderForkHeight);
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
        entityData.set(SPEED_BOOST_TICKS, input.read("SpeedBoostTicks", Codec.INT).orElse(0));
        entityData.set(SPEED_BOOST_STRENGTH, input.read("SpeedBoostStrength", Codec.FLOAT).orElse(DEFAULT_SPEED_BOOST_STRENGTH));
        entityData.set(DATA_FORK_HEIGHT, input.read("ForkHeight", Codec.INT).orElse(MAX_FORK_HEIGHT));
        entityData.set(DATA_SLOW_PREDICATE, input.read("SlowPredicate", BlockPredicate.CODEC));
        entityData.set(DATA_SLOW_MULTIPLIER, input.read("SlowMultiplier", Codec.FLOAT).orElse(0.5f));
        shouldPickupEntities = input.read("ShouldPickupEntities", Codec.BOOL).orElse(true);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        output.putBoolean("RequiresCertification", requiresCertification);
        output.putInt("DriftDuration", driftDuration);
        output.storeNullable("CollisionPredicate", BlockPredicate.CODEC, entityData.get(DATA_COLLISION_PREDICATE).orElse(null));
        output.putInt("SpeedBoostTicks", entityData.get(SPEED_BOOST_TICKS));
        output.putFloat("SpeedBoostStrength", entityData.get(SPEED_BOOST_STRENGTH));
        output.putInt("ForkHeight", entityData.get(DATA_FORK_HEIGHT));
        output.storeNullable("SlowPredicate", BlockPredicate.CODEC, entityData.get(DATA_SLOW_PREDICATE).orElse(null));
        output.putFloat("SlowMultiplier", entityData.get(DATA_SLOW_MULTIPLIER));
        output.putBoolean("ShouldPickupEntities", shouldPickupEntities);
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

        if (PICKUP_DEBUG && level().isClientSide()) {
            Gizmos.cuboid(getPickupAABB(), GizmoStyle.fill(CommonColors.WHITE));
        }

        interpolation.interpolate();
        this.renderForkHeight0 = this.renderForkHeight;
        this.renderForkHeight = this.getForkHeight();
        this.tickWheelRotation();

        if (isLocalInstanceAuthoritative()) {
            applyGravity();

            if(!isDrifting()) {
                applyFriction(FRICTION);
            }

            int speedBoostTicks = entityData.get(SPEED_BOOST_TICKS);
            if (speedBoostTicks > 0) {
                speedBoostTicks--;
                entityData.set(SPEED_BOOST_TICKS, speedBoostTicks);
            }

            if (level().isClientSide()) {
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

        if (isDrifting() && level().isClientSide()) {
            spawnDriftingParticles();
        }

        if (getControllingPassenger() instanceof ServerPlayer player && player.getLastClientInput().backward() && tickCount % SharedConstants.TICKS_PER_SECOND == 0) {
            level().playSound(null, getX(), getY(), getZ(), ExtraSounds.FORKLIFT_REVERSE.value(), getSoundSource());
        }

        move(MoverType.SELF, getDeltaMovement());

        if (shouldPickupEntities) {
            pickupEntitiesInFront();
        }
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

    @Override
    public float maxUpStep() {
        return 0.5f;
    }

    private void applyFriction(double friction) {
        Vec3 velocity = getDeltaMovement();
        setDeltaMovement(velocity.x * friction, velocity.y, velocity.z * friction);
        this.deltaRotation *= friction;
    }

    private void moveFork(int amt) {
        sendForkHeightFromClient(this.getForkHeight() + amt);
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
            if (eject_riders && getForkHeight() > MIN_FORK_HEIGHT && !liftDown) {
                tryEject();
            }

            if (liftUp) {
                moveFork(-1);
            }

            if (liftDown) {
                moveFork(1);
            }

            if (inputLeft) {
                this.deltaRotation--;
            }

            if (inputRight) {
                this.deltaRotation++;
            }

            if (inputRight != inputLeft && !inputUp && !inputDown) {
                f += 0.005F;
            }

            this.setYRot(this.getYRot() + this.deltaRotation);
            if (inputUp) {
                f += 0.05F;
            }

            if (inputDown) {
                f -= 0.02F;
            }

            int speedBoostTicks = entityData.get(SPEED_BOOST_TICKS);
            if (speedBoostTicks > 0) {
                float speedBoostStrength = entityData.get(SPEED_BOOST_STRENGTH);
                f += speedBoostStrength;
            }

            Optional<BlockPredicate> blockPredicate = entityData.get(DATA_SLOW_PREDICATE);
            if (blockPredicate.isPresent() && blockPredicate.get().matches(new BlockInWorld(level(), getOnPos(), false))) {
                float slowMultiplier = entityData.get(DATA_SLOW_MULTIPLIER);
                f *= slowMultiplier;
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
                setDeltaMovement(getDeltaMovement().add(Mth.sin(-this.getYRot() * ((float)Math.PI / 180F)) * f, 0.0F, Mth.cos(this.getYRot() * ((float)Math.PI / 180F)) * f));
            }

            if (isPassenger()) {
                localPlayer.connection.send(ServerboundMoveVehiclePacket.fromEntity(this));
            }
        }
    }

    private void executeDrift(Vec3 travelVector) {
        needsSync = true;

        if (travelVector.lengthSqr() > Mth.EPSILON) {
            float xVelocity = Mth.sin(this.getYRot() * ((float)Math.PI / 180F));
            float zVelocity = Mth.cos(this.getYRot() * ((float)Math.PI / 180F));
            float boost = driftStrength / 10.f;
            setDeltaMovement(getDeltaMovement().add(-xVelocity * boost * DRIFT_FRICTION, 0.0F, zVelocity * boost * DRIFT_FRICTION));
        }
    }

    @Override
    protected void positionRider(Entity entity, Entity.MoveFunction callback) {
        super.positionRider(entity, callback);
        // use the same tag as boats because it's essentially the same thing
        if (!entity.is(EntityTypeTags.CAN_TURN_IN_BOATS)) {
            entity.setYRot(entity.getYRot() + this.deltaRotation);
            entity.setYHeadRot(entity.getYHeadRot() + this.deltaRotation);
            this.refreshAndClampRotationIfDriver(entity);
        }
    }

    @Override
    public void onPassengerTurned(Entity entity) {
        // this prevents the client from having some sort of lag on rotation?
        this.refreshAndClampRotationIfDriver(entity);
    }

    protected void refreshAndClampRotationIfDriver(Entity entity) {
        entity.setYBodyRot(this.getYRot());
        float f = Mth.wrapDegrees(entity.getYRot() - this.getYRot());
        float f1 = getControllingPassenger() == entity ? Mth.clamp(f, -105.0F, 105.0F) : f;
        f = f1 - f;
        entity.yRotO += f;
        entity.setYRot(entity.getYRot() + f);
        entity.setYHeadRot(entity.getYRot());
    }

    public void spawnDriftingParticles() {
        double yawRad = Math.toRadians(this.getYRot());
        double forwardX = -Math.sin(yawRad);
        double forwardZ = Math.cos(yawRad);
        double perpX = Math.cos(yawRad);
        double perpZ = Math.sin(yawRad);

        double baseX = this.getX();
        double baseY = this.getY() + 0.2D;
        double baseZ = this.getZ();

        double intensity = 1.0 + this.driftStrength * 2.0;

        for (int i = 0; i < 2; i++) {
            double sideOffset = (i == 0) ? -0.7D : 0.7D;
            double px = baseX + forwardX * -1.2D + perpX * sideOffset;
            double pz = baseZ + forwardZ * -1.2D + perpZ * sideOffset;

            double vx = -getDeltaMovement().x * 0.5D + (level().getRandom().nextDouble() - 0.5D) * 0.02D;
            double vz = -getDeltaMovement().z * 0.5D + (level().getRandom().nextDouble() - 0.5D) * 0.02D;

            level().addParticle(ExtraParticles.FORK_LIFT_DRIFT_PARTICLE.get(), px, baseY, pz, vx * intensity, 0.02D, vz * intensity);
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

    public void applySpeedBoost(int ticks) {
        entityData.set(SPEED_BOOST_TICKS, ticks);
    }

    @Override
    public final ItemStack getPickResult() {
        return new ItemStack(ExtraItems.FORKLIFT_SPAWN_EGG.get());
    }
}
