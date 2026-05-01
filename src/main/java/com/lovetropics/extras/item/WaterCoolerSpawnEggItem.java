package com.lovetropics.extras.item;

import com.lovetropics.extras.ExtraItems;
import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.entity.ExtraEntities;
import com.lovetropics.extras.entity.ForkliftEntity;
import com.lovetropics.extras.entity.WaterCoolerEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Spawner;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.Objects;

@EventBusSubscriber(modid = LTExtras.MODID)
public class WaterCoolerSpawnEggItem extends Item {
    public WaterCoolerSpawnEggItem(Properties properties) {
        super(properties);
    }

    private static EntityType<WaterCoolerEntity> getType() {
        return ExtraEntities.WATER_COOLER.get();
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        BlockHitResult blockhitresult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
        if (blockhitresult.getType() != HitResult.Type.BLOCK) {
            return InteractionResult.PASS;
        } else if (level instanceof ServerLevel serverLevel) {
            BlockPos pos = blockhitresult.getBlockPos();
            if (!(level.getBlockState(pos).getBlock() instanceof LiquidBlock)) {
                return InteractionResult.PASS;
            } else if (level.mayInteract(player, pos) && player.mayUseItemAt(pos, blockhitresult.getDirection(), itemstack)) {
                Entity entity = getType().spawn(serverLevel, itemstack, player, pos, EntitySpawnReason.SPAWN_ITEM_USE, false, false);
                if (entity == null) {
                    return InteractionResult.PASS;
                } else {
                    itemstack.consume(1, player);
                    player.awardStat(Stats.ITEM_USED.get(this));
                    level.gameEvent(player, GameEvent.ENTITY_PLACE, entity.position());
                    return InteractionResult.SUCCESS;
                }
            } else {
                return InteractionResult.FAIL;
            }
        } else {
            return InteractionResult.SUCCESS;
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (!level.isClientSide()) {
            ItemStack itemstack = context.getItemInHand();
            BlockPos blockpos = context.getClickedPos();
            Direction direction = context.getClickedFace();
            BlockState blockstate = level.getBlockState(blockpos);
            BlockEntity blockEntity = level.getBlockEntity(blockpos);
            if (blockEntity instanceof Spawner spawner) {
                spawner.setEntityId(getType(), level.getRandom());
                level.sendBlockUpdated(blockpos, blockstate, blockstate, 3);
                level.gameEvent(context.getPlayer(), GameEvent.BLOCK_CHANGE, blockpos);
                itemstack.shrink(1);
            } else {
                BlockPos placePos;
                if (blockstate.getCollisionShape(level, blockpos).isEmpty()) {
                    placePos = blockpos;
                } else {
                    placePos = blockpos.relative(direction);
                }

                if (getType().spawn((ServerLevel) level, itemstack, context.getPlayer(), placePos, EntitySpawnReason.SPAWN_ITEM_USE, true, !Objects.equals(blockpos, placePos) && direction == Direction.UP) != null) {
                    itemstack.shrink(1);
                    level.gameEvent(context.getPlayer(), GameEvent.ENTITY_PLACE, blockpos);
                }
            }
        }
        return InteractionResult.SUCCESS;
    }
}
