package com.lovetropics.extras.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.ITag;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.Direction.AxisDirection;
import net.minecraft.util.LazyValue;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GirderBlock extends Block implements IWaterLoggable {

	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	public static final Map<Axis, BooleanProperty> PROPS = Arrays.stream(Axis.values())
			.collect(Maps.toImmutableEnumMap(Function.identity(), a -> BooleanProperty.create(a.getName2())));

	public static final Map<Axis, VoxelShape> BASE_SHAPES = ImmutableMap.<Axis, VoxelShape>builder()
			.put(Axis.X, Block.makeCuboidShape(0, 3, 5, 16, 13, 11))
			.put(Axis.Y, Block.makeCuboidShape(5, 0, 3, 11, 16, 13))
			.put(Axis.Z, Block.makeCuboidShape(5, 3, 0, 11, 13, 16))
			.build();

	private final LazyValue<Map<BlockState, VoxelShape>> ALL_SHAPES = new LazyValue<>(() -> getStateContainer().getValidStates().stream()
			.collect(Collectors.toMap(Function.identity(), s -> {
				VoxelShape ret = VoxelShapes.empty();
				for (Axis a : Axis.values()) {
					if (s.get(PROPS.get(a))) {
						ret = VoxelShapes.or(ret, BASE_SHAPES.get(a));
					}
				}
				return ret;
			})));

	private final ITag<Block> connectionTag;

	public GirderBlock(ITag<Block> connectionTag, Properties properties) {
		super(properties);
		this.connectionTag = connectionTag;
		setDefaultState(PROPS.keySet().stream()
				.reduce(getStateContainer().getBaseState(), (s, a) -> s.with(PROPS.get(a), false), (s1, s2) -> s1)
				.with(WATERLOGGED, false));
	}

	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		builder.add(PROPS.values().toArray(new BooleanProperty[0])).add(WATERLOGGED);
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
		return !state.get(WATERLOGGED);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return ALL_SHAPES.getValue().get(state);
	}

	@Override
	@Deprecated
	public FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
	}

	@Override
	public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
		return false;
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		BlockState state = super.getStateForPlacement(context);
		FluidState ifluidstate = context.getWorld().getFluidState(context.getPos());
		boolean connected = false;
		for (Direction dir : Direction.values()) {
			if (context.getWorld().getBlockState(context.getPos().offset(dir)).isIn(connectionTag)) {
				state = state.with(PROPS.get(dir.getAxis()), true);
				connected = true;
			}
		}
		if (!connected) {
			state = state.with(PROPS.get(context.getFace().getAxis()), true);
		}
		return state.with(WATERLOGGED, ifluidstate.getFluid() == Fluids.WATER);
	}

	@Override
	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn,
			BlockPos currentPos, BlockPos facingPos) {
		if (stateIn.get(WATERLOGGED)) {
			worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
		}
		BlockState ret = stateIn;
		boolean connected = false;
		for (Axis a : Axis.values()) {
			if (worldIn.getBlockState(currentPos.offset(Direction.getFacingFromAxis(AxisDirection.NEGATIVE, a))).isIn(connectionTag)
			 || worldIn.getBlockState(currentPos.offset(Direction.getFacingFromAxis(AxisDirection.POSITIVE, a))).isIn(connectionTag)) {
				connected = true;
				ret = ret.with(PROPS.get(a), true);
			} else {
				ret = ret.with(PROPS.get(a), false);
			}
		}
		if (!connected) {
			return stateIn;
		}
		return ret;
	}
}
