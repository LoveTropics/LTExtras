package com.lovetropics.extras.entity;

import com.lovetropics.extras.network.message.ServerboundSwingPacket;
import com.mojang.authlib.GameProfile;
import com.mojang.serialization.Codec;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
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
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public abstract class BlockFormationEntity extends Entity implements IEntityWithComplexSpawn, PlayerRideable {

    private BlockFormation blockFormation;

    public BlockFormationEntity(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        Optional<BlockFormation> blockFormation1 = input.read("BlockFormation", BlockFormation.CODEC);
        blockFormation1.ifPresent(formation -> this.blockFormation = formation);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        output.store("BlockFormation", BlockFormation.CODEC, blockFormation);
    }

    @Override
    public void writeSpawnData(RegistryFriendlyByteBuf buffer) {
        try {
            ByteBufCodecs.fromCodec(BlockFormation.CODEC.fieldOf("formation").codec()).encode(buffer, blockFormation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void readSpawnData(RegistryFriendlyByteBuf additionalData) {
        try {
            blockFormation = ByteBufCodecs.fromCodec(BlockFormation.CODEC.fieldOf("formation").codec()).decode(additionalData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setBlockFormation(BlockFormation blockFormation){
        this.blockFormation = blockFormation;
    }

    public void deform(){
        blockFormation.addBlocksToLevel(this.level());
        discard();
    }

    public BlockFormation getBlockFormation(){
        return blockFormation;
    }

    @Override
    public void setPos(double x, double y, double z) {
        super.setPos(x, y, z);
        if(blockFormation == null)
            return;
        setBoundingBox(getBlockFormation().getBounding().move(getBlockFormation().getAnchorBlock()));
    }


    @Override
    public boolean isPickable() {
        return true;
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
        return getPassengers().size() < getBlockFormation().getSeats().size();
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand, Vec3 location) {
        if (!level().isClientSide() && !player.isShiftKeyDown()) {
            if(!getBlockFormation().getSeats().isEmpty()){
                player.startRiding(this);
                return InteractionResult.SUCCESS;
            }
        } else if(!level().isClientSide() && player.isShiftKeyDown()){
            deform();
            return InteractionResult.SUCCESS;
        }

        return !player.isPassengerOfSameVehicle(this) ? InteractionResult.SUCCESS : InteractionResult.PASS;
    }

    @Override
    protected Vec3 getPassengerAttachmentPoint(Entity passenger, EntityDimensions dimensions, float scale) {
        if(getPassengers().isEmpty()){
            return Vec3.atCenterOf(getBlockFormation().getSeats().get(0)
                    .offset(getBlockFormation().getAnchorBlock())).subtract(position()).subtract(0, 0.5, 0);
        }
        int index = getPassengers().indexOf(passenger);
        return Vec3.atCenterOf(getBlockFormation().getSeats().get(index)
                .offset(getBlockFormation().getAnchorBlock())).subtract(position()).subtract(0, 0.5, 0);
    }
}

