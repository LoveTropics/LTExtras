package com.lovetropics.extras.block.entity;

import net.minecraft.SharedConstants;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class DisplayBlockEntity extends BlockEntity {

    private static final String ITEM = "item";
    private static final String DISPLAY_ITEM = "display_item";
    private static final String FILTER = "filter";
    private static final String TIME_TO_CONVERT = "time_to_convert";
    private static final String CONVERSION_PROGRESS = "conversion_progress";
    private static final String CONVERSION_OUTPUT = "conversion_output";

    private static final int DEFAULT_TIME_TO_CONVERT = SharedConstants.TICKS_PER_SECOND * 5;

    private ItemStack itemStack = ItemStack.EMPTY;
    private ItemStack displayItemStack = ItemStack.EMPTY;
    private ItemPredicate filter = ItemPredicate.Builder.item().build();
    private int timeToConvert = DEFAULT_TIME_TO_CONVERT;
    private int conversionProgress = 0;
    private ItemStack conversionOutput = ItemStack.EMPTY;

    public DisplayBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, DisplayBlockEntity displayBlockEntity) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        if (displayBlockEntity.itemStack.isEmpty() || displayBlockEntity.conversionOutput.isEmpty() || displayBlockEntity.itemStack.is(displayBlockEntity.conversionOutput.getItem())) {
            return;
        }

        displayBlockEntity.conversionProgress++;
        BlockPos blockPos = displayBlockEntity.getBlockPos();
        serverLevel.sendParticles(ParticleTypes.SPLASH.getType(), blockPos.getX() + 0.5, blockPos.getY() + 1.25, blockPos.getZ() + 0.5, 5, 0.0D, 0.0D, 0.0D, 1);
        if (displayBlockEntity.conversionProgress >= displayBlockEntity.timeToConvert) {
            displayBlockEntity.itemStack = displayBlockEntity.conversionOutput.copy();
            displayBlockEntity.conversionProgress = 0;
        }
        displayBlockEntity.level.sendBlockUpdated(displayBlockEntity.getBlockPos(), displayBlockEntity.getBlockState(), displayBlockEntity.getBlockState(), 3);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        CompoundTag compoundtag = super.getUpdateTag(provider);
        RegistryOps<Tag> registryops = provider.createSerializationContext(NbtOps.INSTANCE);
        if (!this.itemStack.isEmpty()) {
            compoundtag.store(DisplayBlockEntity.ITEM, ItemStack.CODEC, registryops, this.itemStack);
        }
        if (!this.displayItemStack.isEmpty()) {
            compoundtag.store(DisplayBlockEntity.DISPLAY_ITEM, ItemStack.CODEC, registryops, this.displayItemStack);
        }
        compoundtag.putInt(DisplayBlockEntity.TIME_TO_CONVERT, this.timeToConvert);
        compoundtag.putInt(DisplayBlockEntity.CONVERSION_PROGRESS, this.conversionProgress);
        if (!this.conversionOutput.isEmpty()) {
            compoundtag.store(DisplayBlockEntity.CONVERSION_OUTPUT, ItemStack.CODEC, registryops, this.conversionOutput);
        }

        return compoundtag;
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.itemStack = input.read(DisplayBlockEntity.ITEM, ItemStack.CODEC).orElse(ItemStack.EMPTY);
        this.displayItemStack = input.read(DisplayBlockEntity.DISPLAY_ITEM, ItemStack.CODEC).orElse(ItemStack.EMPTY);
        this.timeToConvert = input.getIntOr(TIME_TO_CONVERT, DEFAULT_TIME_TO_CONVERT);
        this.conversionProgress = input.getIntOr(CONVERSION_PROGRESS, 0);
        this.conversionOutput = input.read(CONVERSION_OUTPUT, ItemStack.CODEC).orElse(ItemStack.EMPTY);
        this.filter = input.read(FILTER, ItemPredicate.CODEC).orElse(ItemPredicate.Builder.item().build());
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if (!this.itemStack.isEmpty()) {
            output.store(ITEM, ItemStack.CODEC, this.itemStack);
        }
        if (!this.displayItemStack.isEmpty()) {
            output.store(DISPLAY_ITEM, ItemStack.CODEC, this.displayItemStack);
        }
        if (!this.conversionOutput.isEmpty()) {
            output.store(CONVERSION_OUTPUT, ItemStack.CODEC, this.conversionOutput);
        }
        output.putInt(CONVERSION_PROGRESS, this.conversionProgress);
        output.store(FILTER, ItemPredicate.CODEC, this.filter);
        output.putInt(TIME_TO_CONVERT, this.timeToConvert);
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
        level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
    }

    public ItemStack getDisplayItemStack() {
        return displayItemStack;
    }

    public ItemPredicate getFilter() {
        return filter;
    }

    public void setConversionProgress(int conversionProgress) {
        this.conversionProgress = conversionProgress;
        setChanged();
        level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
    }
}
