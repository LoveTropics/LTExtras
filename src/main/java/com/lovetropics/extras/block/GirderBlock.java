package com.lovetropics.extras.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.tags.TagKey;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GirderBlock extends Block implements SimpleWaterloggedBlock {

	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	public static final Map<Axis, BooleanProperty> PROPS = Arrays.stream(Axis.values())
			.collect(Maps.<Axis, Axis, BooleanProperty>toImmutableEnumMap(Function.identity(), a -> BooleanProperty.create(a.getName())));

	public static final Map<Axis, VoxelShape> BASE_SHAPES = ImmutableMap.<Axis, VoxelShape>builder()
			.put(Axis.X, Block.box(0, 3, 5, 16, 13, 11))
			.put(Axis.Y, Block.box(5, 0, 3, 11, 16, 13))
			.put(Axis.Z, Block.box(5, 3, 0, 11, 13, 16))
			.build();

	private final LazyLoadedValue<Map<BlockState, VoxelShape>> ALL_SHAPES = new LazyLoadedValue<>(() -> getStateDefinition().getPossibleStates().stream()
			.collect(Collectors.toMap(Function.identity(), s -> {
				VoxelShape ret = Shapes.empty();
				for (Axis a : Axis.values()) {
					if (s.getValue(PROPS.get(a))) {
						ret = Shapes.or(ret, BASE_SHAPES.get(a));
					}
				}
				return ret;
			})));

	private final TagKey<Block> connectionTag;

	public GirderBlock(TagKey<Block> connectionTag, Properties properties) {
		super(properties);
		this.connectionTag = connectionTag;
		registerDefaultState(PROPS.keySet().stream()
				.reduce(getStateDefinition().any(), (s, a) -> s.setValue(PROPS.get(a), false), (s1, s2) -> s1)
				.setValue(WATERLOGGED, false));
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(PROPS.values().toArray(new BooleanProperty[0])).add(WATERLOGGED);
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
		return !state.getValue(WATERLOGGED);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return ALL_SHAPES.get().get(state);
	}

	@Override
	@Deprecated
	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	public boolean isPathfindable(BlockState state, BlockGetter worldIn, BlockPos pos, PathComputationType type) {
		return false;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockState state = super.getStateForPlacement(context);
		FluidState ifluidstate = context.getLevel().getFluidState(context.getClickedPos());
		boolean connected = false;
		for (Direction dir : Direction.values()) {
			if (context.getLevel().getBlockState(context.getClickedPos().relative(dir)).is(connectionTag)) {
				state = state.setValue(PROPS.get(dir.getAxis()), true);
				connected = true;
			}
		}
		if (!connected) {
			state = state.setValue(PROPS.get(context.getClickedFace().getAxis()), true);
		}
		return state.setValue(WATERLOGGED, ifluidstate.getType() == Fluids.WATER);
	}

	@Override
	public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn,
								  BlockPos currentPos, BlockPos facingPos) {
		if (stateIn.getValue(WATERLOGGED)) {
			worldIn.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
		}
		BlockState ret = stateIn;
		boolean connected = false;
		for (Axis a : Axis.values()) {
			if (worldIn.getBlockState(currentPos.relative(Direction.get(AxisDirection.NEGATIVE, a))).is(connectionTag)
					|| worldIn.getBlockState(currentPos.relative(Direction.get(AxisDirection.POSITIVE, a))).is(connectionTag)) {
				connected = true;
				ret = ret.setValue(PROPS.get(a), true);
			} else {
				ret = ret.setValue(PROPS.get(a), false);
			}
		}
		if (!connected) {
			return stateIn;
		}
		return ret;
	}
}
