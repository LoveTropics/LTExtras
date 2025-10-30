package com.lovetropics.extras.block;

import com.lovetropics.extras.ExtraBlocks;
import com.lovetropics.lib.permission.PermissionsApi;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public final class RoleBarrierBlock extends CustomBarrierBlock implements EntityBlock {
    public RoleBarrierBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        // We don't have permissions available on the server, so let the client assume they can
        // always pass through the block... if the server says no, they will attempt and will be placed back a few centimetres
        // Not the best experience, but it works fine enough
        if (world instanceof Level lv && lv.isClientSide()) return Shapes.empty();

        if (world.getBlockEntity(pos) instanceof RoleBarrierBE be) {
            boolean allow = be.blacklist;
            if (context instanceof EntityCollisionContext ctx) {
                for (var role : PermissionsApi.lookup().byEntity(ctx.getEntity())) {
                    if (be.roles.contains(role.id())) {
                        allow = !allow;
                        break;
                    }
                }
            }
            if (allow) {
                return Shapes.empty();
            }
        }

        return super.getCollisionShape(state, world, pos, context);
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return getShape(state, world, pos, context);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new RoleBarrierBE(ExtraBlocks.ROLE_BARRIER.getSibling(Registries.BLOCK_ENTITY_TYPE).get(), blockPos, blockState);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        var stackName = stack.getCustomName();
        if (level.getBlockEntity(pos) instanceof RoleBarrierBE be && stackName != null) {
            var name = stackName.getString();

            boolean blacklist = false;
            if (name.startsWith("!")) {
                name = name.substring(1);
                blacklist = true;
            }

            be.blacklist = blacklist;
            be.roles = Arrays.stream(name.split(","))
                    .map(String::trim)
                    .toList();
            be.setChanged();
        }
    }

    public static final class RoleBarrierBE extends BlockEntity {
        private List<String> roles = List.of();
        private boolean blacklist;

        public RoleBarrierBE(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
            super(type, pos, blockState);
        }

        @Override
        protected void saveAdditional(ValueOutput output) {
            super.saveAdditional(output);
            output.putBoolean("blacklist", blacklist);
            var roles = output.list("roles", Codec.STRING);
            this.roles.forEach(roles::add);
        }

        @Override
        protected void loadAdditional(ValueInput input) {
            super.loadAdditional(input);
            this.blacklist = input.getBooleanOr("blacklist", false);
            this.roles = input.listOrEmpty("roles", Codec.STRING).stream().toList();
        }
    }
}
