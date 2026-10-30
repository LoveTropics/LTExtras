package com.lovetropics.extras.zipline;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * Sits on the lower half of a pole, so that its ropes are taken down however the pole is removed, even by commands that
 * don't update neighbours. The ropes themselves are kept in the {@link ZiplineNetwork}.
 */
public class ZiplinePoleBlockEntity extends BlockEntity {
    // Ropes used to be stored on both of their poles: they are moved over to the network once the pole is loaded
    private List<LegacyConnection> legacyConnections = List.of();

    public ZiplinePoleBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public Vec3 getAnchorOffset() {
        return ZiplinePoleBlock.getAnchorOffset(getBlockState());
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (!legacyConnections.isEmpty() && level instanceof ServerLevel) {
            ZiplineNetwork network = ZiplineNetwork.get(level);
            for (LegacyConnection connection : legacyConnections) {
                if (!network.isConnected(worldPosition, connection.target())) {
                    network.connect(worldPosition, getAnchorOffset(), connection.target(), connection.targetAnchorOffset(), connection.settings());
                }
            }
            legacyConnections = List.of();
            setChanged();
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        legacyConnections = input.read("connections", LegacyConnection.CODEC.listOf()).orElse(List.of());
    }

    private record LegacyConnection(BlockPos target, Vec3 targetAnchorOffset, ZiplineSettings settings) {
        private static final Codec<LegacyConnection> CODEC = RecordCodecBuilder.create(i -> i.group(
                BlockPos.CODEC.fieldOf("target").forGetter(LegacyConnection::target),
                Vec3.CODEC.optionalFieldOf("target_anchor", ZiplinePoleBlock.FLOOR_ANCHOR).forGetter(LegacyConnection::targetAnchorOffset),
                ZiplineSettings.MAP_CODEC.forGetter(LegacyConnection::settings)
        ).apply(i, LegacyConnection::new));
    }
}
