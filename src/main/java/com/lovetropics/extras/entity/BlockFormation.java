package com.lovetropics.extras.entity;

import com.lovetropics.extras.ExtraBlocks;
import com.lovetropics.extras.ExtraTags;
import com.lovetropics.lib.codec.MoreCodecs;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;

public class BlockFormation {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final Codec<StructureTemplate.StructureBlockInfo> STRUCTURE_BLOCK_INFO_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    BlockPos.CODEC.fieldOf("structurePos").forGetter(StructureTemplate.StructureBlockInfo::pos),
                    BlockState.CODEC.fieldOf("state").forGetter(StructureTemplate.StructureBlockInfo::state),
                    CompoundTag.CODEC.optionalFieldOf("data").forGetter((c) -> Optional.ofNullable(c.nbt()))
            ).apply(instance, (pos, blockstate, nbt)
                               -> new StructureTemplate.StructureBlockInfo(pos, blockstate, nbt.orElse(null))));

    public static final Codec<BlockFormation> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.pair(Codec.LONG.fieldOf("pos").codec(), STRUCTURE_BLOCK_INFO_CODEC).listOf().xmap(pairs -> {
                        Map<BlockPos, StructureTemplate.StructureBlockInfo> map = new HashMap<>();
                        for (com.mojang.datafixers.util.Pair<Long, StructureTemplate.StructureBlockInfo> pair : pairs) {
                            map.put(BlockPos.of(pair.getFirst()), pair.getSecond());
                        }
                        return map;
                    }, (pairs) -> {
                        List<com.mojang.datafixers.util.Pair<Long, StructureTemplate.StructureBlockInfo>> list = new ArrayList<>();
                        for (BlockPos blockPos : pairs.keySet()) {
                            list.add(com.mojang.datafixers.util.Pair.of(blockPos.asLong(), pairs.get(blockPos)));
                        }
                        return list;
                    }).fieldOf("blocks").forGetter(o -> o.blocks),
                    BlockPos.CODEC.optionalFieldOf("anchor").forGetter(o -> Optional.ofNullable(o.anchorBlock)),
                    MoreCodecs.AABB.fieldOf("bounding").forGetter(BlockFormation::getBounding),
                    BlockPos.CODEC.listOf().fieldOf("seats").forGetter(formation -> formation.seats)
            ).apply(instance, BlockFormation::new));

    private Map<BlockPos, StructureTemplate.StructureBlockInfo> blocks;
    private List<BlockPos> seats;
    private @Nullable BlockPos anchorBlock;
    private AABB bounding;

    public BlockFormation(){
        blocks = new HashMap<>();
        seats = new ArrayList<>();
    }

    private BlockFormation(Map<BlockPos, StructureTemplate.StructureBlockInfo> blocks,
                           Optional<BlockPos> anchorBlock, AABB bounding, List<BlockPos> seats){
        this.blocks = blocks;
        this.anchorBlock = anchorBlock.orElse(null);
        this.bounding = bounding;
        this.seats = seats;
    }

    public @Nullable BlockPos getAnchorBlock() {
        return anchorBlock;
    }

    public AABB getBounding() {
        return bounding;
    }

    public List<BlockPos> getSeats() {
        return seats;
    }

    public boolean form(Level level, BlockPos anchorBlock) throws Exception {
        this.anchorBlock = anchorBlock;
        Queue<BlockPos> blockPosQueue = new LinkedList<>();
        Set<BlockPos> visitedBlocks = new HashSet<>();
        blockPosQueue.add(anchorBlock);

        if (bounding == null)
            bounding = new AABB(BlockPos.ZERO);
        for(int limit = 0; limit < 100; limit++){
            if(blockPosQueue.isEmpty())
                return true;
            if(!formBlock(level, blockPosQueue, visitedBlocks))
                return false;
        }
        throw new Exception("Too big!");
    }

    private boolean formBlock(Level level, Queue<BlockPos> blockPosQueue, Set<BlockPos> visitedBlocks) throws Exception{
        BlockPos pos = blockPosQueue.poll();
        if(pos == null) return false;
        visitedBlocks.add(pos);

        if(this.anchorBlock.equals(pos)) {
            blockPosQueue.add(pos.above());
            return true;
        }

        BlockState currentBlockState = level.getBlockState(pos);
        if(currentBlockState.is(ExtraTags.Blocks.SEAT)){
            addSeat(pos);
        }

        for (Direction direction : Direction.values()) {
            BlockPos relativePos = pos.relative(direction);
            BlockState blockState = level.getBlockState(relativePos);
            if(blockState.isAir())
                continue;
            if(!visitedBlocks.contains(relativePos))
                blockPosQueue.add(relativePos);
        }
        addBlock(level, pos, captureBlock(level, pos));
        if(blocks.size() < 100)
            return true;
        throw new Exception("Too big!");
    }

    private Pair<StructureTemplate.StructureBlockInfo, @Nullable BlockEntity> captureBlock(Level level, BlockPos pos){
        BlockState blockState = level.getBlockState(pos);
        BlockEntity blockEntity = level.getBlockEntity(pos);
        CompoundTag blockEntityNBTData = getBlockEntityNBTData(level, blockEntity);
        return Pair.of(new StructureTemplate.StructureBlockInfo(pos, blockState, blockEntityNBTData), blockEntity);
    }

    private void addSeat(BlockPos pos){
        BlockPos localPos = pos.subtract(this.anchorBlock);
        if(seats.contains(localPos))
            return;
        seats.add(localPos);
    }

    private void addBlock(Level level, BlockPos pos, Pair<StructureTemplate.StructureBlockInfo, @Nullable BlockEntity> blockData){
        if(this.anchorBlock == null)
            return;
        BlockPos localPos = pos.subtract(this.anchorBlock);
        if(blocks.put(localPos, blockData.getKey()) != null)
            return;
        bounding = bounding.minmax(new AABB(localPos));
        //TODO: calculate bounds
    }

    private @Nullable CompoundTag getBlockEntityNBTData(Level level, @Nullable BlockEntity blockEntity){
        if(blockEntity == null) return null;
        CompoundTag compoundTag = blockEntity.saveWithFullMetadata(level.registryAccess());
        compoundTag.remove("x");
        compoundTag.remove("y");
        compoundTag.remove("z");
        return compoundTag;
    }

    public void removeBlocksFromLevel(Level level){
        for (BlockPos blockPos : blocks.keySet()) {
            BlockPos offset = blockPos.offset(anchorBlock);
            level.removeBlockEntity(offset);
            int flags = Block.UPDATE_MOVE_BY_PISTON | Block.UPDATE_SUPPRESS_DROPS | Block.UPDATE_KNOWN_SHAPE
                    | Block.UPDATE_CLIENTS | Block.UPDATE_IMMEDIATE;
            level.setBlock(offset, Blocks.AIR.defaultBlockState(), flags);
        }
    }

    public void addBlocksToLevel(Level level){
        for (BlockPos blockPos : blocks.keySet()) {
            StructureTemplate.StructureBlockInfo structureBlockInfo = blocks.get(blockPos);
            BlockState state = structureBlockInfo.state();
            BlockPos newPos = blockPos.offset(anchorBlock);
            level.setBlock(newPos, state, Block.UPDATE_MOVE_BY_PISTON | Block.UPDATE_ALL);

            BlockEntity blockEntity = level.getBlockEntity(newPos);
            CompoundTag nbt = structureBlockInfo.nbt();
            if(blockEntity != null && nbt != null){
                nbt.putInt("x", newPos.getX());
                nbt.putInt("y", newPos.getY());
                nbt.putInt("z", newPos.getZ());
                try (ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(blockEntity.problemPath(), LOGGER)) {
                    blockEntity.loadWithComponents(TagValueInput.create(reporter, blockEntity.getLevel().registryAccess(), nbt));
                }
            }
        }
    }

    public Map<BlockPos, StructureTemplate.StructureBlockInfo> getBlocks() {
        return blocks;
    }
}
