package com.lovetropics.extras.entity;

import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

public class CleaningItemFrame extends ItemFrame {

    private static final String CLEAN_TIME = "clean_time";
    private static final String MAX_CLEAN_TIME = "max_clean_time";
    private static final String CLEANED_ITEM = "cleaned_item";
    private static final String CLEANING_ITEM_PREDICATE = "cleaning_item_predicate";
    private static final String AUTOMATED = "automated";

    private static final EntityDataAccessor<Integer> DATA_CLEAN_TICK = SynchedEntityData.defineId(CleaningItemFrame.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_MAX_CLEAN_TICK = SynchedEntityData.defineId(CleaningItemFrame.class, EntityDataSerializers.INT);

    private static final ItemPredicate DEFAULT_PREDICATE = ItemPredicate.Builder.item().build();

    private boolean automated = false;
    private ItemStack cleanedItemOutput = ItemStack.EMPTY;
    private ItemPredicate cleaningItemPredicate = DEFAULT_PREDICATE;

    public CleaningItemFrame(EntityType<? extends ItemFrame> entityType, Level level) {
        super(entityType, level);
    }

    private CleaningItemFrame(EntityType<? extends ItemFrame> entityType, Level level, BlockPos pos, Direction direction) {
        super(entityType, level, pos, direction);
    }

    public static CleaningItemFrame create(Level level, BlockPos pos, Direction direction) {
        return new CleaningItemFrame(ExtraEntities.CLEANING_ITEM_FRAME.get(), level, pos, direction);
    }

    @Override
    public void tick() {
        if (this.level() instanceof ServerLevel level) {
            ItemStack itemInFrame = this.getItem();
            if (itemInFrame.is(cleanedItemOutput.getItem())) {
                return;
            }
            if (!this.cleaningItemPredicate.test(itemInFrame) || itemInFrame.isEmpty()) {
                this.setCleanTick(0);
                return;
            }
            if (automated) {
                this.processCleanTick(level);
            }
        }
        super.tick();
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand, Vec3 location) {
        if (hand == InteractionHand.MAIN_HAND && this.level() instanceof ServerLevel serverLevel) {
            ItemStack itemInFrame = this.getItem();
            if (!itemInFrame.isEmpty()) {
                if (!automated) {
                    this.processCleanTick(serverLevel);
                    return InteractionResult.SUCCESS;
                }
                return InteractionResult.PASS;
            }
            ItemStack stackInHand = player.getItemInHand(hand);
            if (cleaningItemPredicate.test(stackInHand) && !stackInHand.isEmpty()) {
                this.setItem(stackInHand);
                stackInHand.consume(1, player);
                this.gameEvent(GameEvent.BLOCK_CHANGE, player);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    public void processCleanTick(ServerLevel serverLevel) {
        ItemStack currentItem = this.getItem();

        if (currentItem.is(cleanedItemOutput.getItem())) {
            return;
        }

        int cleanTick = this.getCleanTick();
        int maxCleanTick = this.getMaxCleanTick();

        if (cleanTick < maxCleanTick) {
            serverLevel.sendParticles(ParticleTypes.SPLASH.getType(), this.getX(), this.getY(), this.getZ(), 5, 0.0D, 0.0D, 0.0D, 1);
            this.setCleanTick(cleanTick + 1);
        }

        if (cleanTick == maxCleanTick) {
            this.setItem(cleanedItemOutput.copy());
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_CLEAN_TICK, 0);
        builder.define(DATA_MAX_CLEAN_TICK, 100);
    }

    public int getCleanTick() {
        return this.entityData.get(DATA_CLEAN_TICK);
    }

    public void setCleanTick(int tick) {
        this.entityData.set(DATA_CLEAN_TICK, tick);
    }

    public int getMaxCleanTick() {
        return this.entityData.get(DATA_MAX_CLEAN_TICK);
    }

    public void setMaxCleanTick(int tick) {
        this.entityData.set(DATA_MAX_CLEAN_TICK, tick);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput valueOutput) {
        super.addAdditionalSaveData(valueOutput);
        valueOutput.putInt(CleaningItemFrame.CLEAN_TIME, this.getCleanTick());
        valueOutput.putInt(CleaningItemFrame.MAX_CLEAN_TIME, this.getMaxCleanTick());
        valueOutput.store(CleaningItemFrame.CLEANED_ITEM, ItemStack.CODEC, this.cleanedItemOutput);
        valueOutput.store(CleaningItemFrame.CLEANING_ITEM_PREDICATE, ItemPredicate.CODEC, this.cleaningItemPredicate);
        valueOutput.putBoolean(CleaningItemFrame.AUTOMATED, this.automated);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput valueInput) {
        super.readAdditionalSaveData(valueInput);
        setCleanTick(valueInput.getInt(CleaningItemFrame.CLEAN_TIME).orElse(0));
        setMaxCleanTick(valueInput.getInt(CleaningItemFrame.MAX_CLEAN_TIME).orElse(100));
        this.cleanedItemOutput = valueInput.read(CleaningItemFrame.CLEANED_ITEM, ItemStack.CODEC).orElse(ItemStack.EMPTY);
        this.cleaningItemPredicate = valueInput.read(CleaningItemFrame.CLEANING_ITEM_PREDICATE, ItemPredicate.CODEC).orElse(DEFAULT_PREDICATE);
        this.automated = valueInput.getBooleanOr(CleaningItemFrame.AUTOMATED, false);
    }
}
