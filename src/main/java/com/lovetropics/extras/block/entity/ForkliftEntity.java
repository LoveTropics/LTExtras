package com.lovetropics.extras.block.entity;

import com.lovetropics.extras.ExtraItems;
import com.lovetropics.extras.ExtraLangKeys;
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

    private static final Component CERTIFICATION_MISSING = ExtraLangKeys.FORKLIFT_CERTIFICATION_MISSING.get().withStyle(ChatFormatting.RED);

    private static final int MAX_PASSENGERS = 3;
    private static final int MIN_FORK_HEIGHT = -5;
    private static final int MAX_FORK_HEIGHT = 20;
    private static final float RIDER_X_OFFSET = 0.3f;
    private static final float RIDER_Z_OFFSET = 2.0f;
    public static final float FORKLIFT_SCALE = 1.2f;
    public static final double FRICTION = 0.92f;

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

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        requiresCertification = input.read("RequiresCertification", Codec.BOOL).orElse(false);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        output.putBoolean("RequiresCertification", requiresCertification);
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
            applyFriction();
            if (level().isClientSide) {
                controlForklift();
            }

            move(MoverType.SELF, getDeltaMovement());
        } else {
            setDeltaMovement(Vec3.ZERO);
        }
    }

    private void applyFriction() {
        Vec3 velocity = getDeltaMovement();
        setDeltaMovement(velocity.x * FRICTION, velocity.y, velocity.z * FRICTION);
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

            boolean liftUp = localPlayer.input.keyPresses.sprint();
            boolean liftDown = localPlayer.input.keyPresses.jump();

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
                f += 0.04F;
            }

            if (inputDown) {
                f -= 0.04F;
            }

            setDeltaMovement(getDeltaMovement().add(Mth.sin(-this.getYRot() * ((float)Math.PI / 180F)) * f, 0.0F, Mth.cos(this.getYRot() * ((float)Math.PI / 180F)) * f));

            if (isPassenger()) {
                localPlayer.connection.send(ServerboundMoveVehiclePacket.fromEntity(this));
            }
        }
    }
}
