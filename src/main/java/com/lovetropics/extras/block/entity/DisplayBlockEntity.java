package com.lovetropics.extras.block.entity;

import net.minecraft.SharedConstants;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class DisplayBlockEntity extends BlockEntity {

    private static final String ITEM = "item";
    private static final String FILTER = "filter";
    private static final String TIME_TO_CONVERT = "time_to_convert";
    private static final String CONVERSION_PROGRESS = "conversion_progress";
    private static final String COMPONENTS_TO_REMOVE = "components_to_remove";

    private static final int DEFAULT_TIME_TO_CONVERT = SharedConstants.TICKS_PER_SECOND * 5;

    private ItemStack itemStack = ItemStack.EMPTY;
    private ItemPredicate filter = ItemPredicate.Builder.item().build();
    private int timeToConvert = DEFAULT_TIME_TO_CONVERT;
    private int conversionProgress = 0;
    private List<DataComponentType<?>> componentsToRemove = new ArrayList<>();

    public DisplayBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, DisplayBlockEntity displayBlockEntity) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        Optional<ItemStack> outputItemOpt = displayBlockEntity.getOutputItem();
        if (displayBlockEntity.itemStack.isEmpty() || outputItemOpt.isEmpty() || ItemStack.isSameItemSameComponents(displayBlockEntity.itemStack, outputItemOpt.get())) {
            return;
        }

        displayBlockEntity.conversionProgress++;
        BlockPos blockPos = displayBlockEntity.getBlockPos();
        serverLevel.sendParticles(ParticleTypes.SPLASH.getType(), blockPos.getX() + 0.5, blockPos.getY() + 1.25, blockPos.getZ() + 0.5, 5, 0.0D, 0.0D, 0.0D, 1);
        if (displayBlockEntity.conversionProgress >= displayBlockEntity.timeToConvert) {
            displayBlockEntity.itemStack = outputItemOpt.get().copy();
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
        compoundtag.putInt(DisplayBlockEntity.TIME_TO_CONVERT, this.timeToConvert);
        compoundtag.putInt(DisplayBlockEntity.CONVERSION_PROGRESS, this.conversionProgress);
        compoundtag.store(DisplayBlockEntity.FILTER, ItemPredicate.CODEC, registryops, this.filter);
        compoundtag.store(DisplayBlockEntity.COMPONENTS_TO_REMOVE, DataComponentType.CODEC.listOf(), registryops, this.componentsToRemove);

        return compoundtag;
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.itemStack = input.read(DisplayBlockEntity.ITEM, ItemStack.CODEC).orElse(ItemStack.EMPTY);
        this.timeToConvert = input.getIntOr(DisplayBlockEntity.TIME_TO_CONVERT, DEFAULT_TIME_TO_CONVERT);
        this.conversionProgress = input.getIntOr(DisplayBlockEntity.CONVERSION_PROGRESS, 0);
        this.filter = input.read(DisplayBlockEntity.FILTER, ItemPredicate.CODEC).orElse(ItemPredicate.Builder.item().build());
        this.componentsToRemove = input.read(DisplayBlockEntity.COMPONENTS_TO_REMOVE, DataComponentType.CODEC.listOf()).orElse(Collections.emptyList());
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if (!this.itemStack.isEmpty()) {
            output.store(ITEM, ItemStack.CODEC, this.itemStack);
        }
        output.putInt(CONVERSION_PROGRESS, this.conversionProgress);
        output.store(FILTER, ItemPredicate.CODEC, this.filter);
        output.putInt(TIME_TO_CONVERT, this.timeToConvert);
        output.store(COMPONENTS_TO_REMOVE, DataComponentType.CODEC.listOf(), this.componentsToRemove);
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
        level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
    }

    public ItemPredicate getFilter() {
        return filter;
    }

    public void setConversionProgress(int conversionProgress) {
        this.conversionProgress = conversionProgress;
        setChanged();
        level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
    }

    public Optional<ItemStack> getOutputItem() {
        if (componentsToRemove.isEmpty()) {
            return Optional.empty();
        }
        ItemStack outputStack = itemStack.copy();
        for (DataComponentType<?> componentType : componentsToRemove) {
            outputStack.remove(componentType);
        }
        return Optional.of(outputStack);
    }
}
