package com.lovetropics.extras.zipline;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Sits on the lower half of a pole. Both poles of a rope store the connection to each other.
 */
public class ZiplinePoleBlockEntity extends BlockEntity {
    public static final int MAX_CONNECTIONS = 2;

    private static final String TAG_CONNECTIONS = "connections";

    private final List<ZiplineConnection> connections = new ArrayList<>(MAX_CONNECTIONS);
    private final List<ZiplineConnection> connectionsView = Collections.unmodifiableList(connections);

    public ZiplinePoleBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public static boolean connect(Level level, BlockPos from, BlockPos to, int slack) {
        if (level.getBlockEntity(from) instanceof ZiplinePoleBlockEntity fromPole && level.getBlockEntity(to) instanceof ZiplinePoleBlockEntity toPole) {
            fromPole.addConnection(new ZiplineConnection(to, slack));
            toPole.addConnection(new ZiplineConnection(from, slack));
            return true;
        }
        return false;
    }

    public static void disconnect(Level level, BlockPos from, BlockPos to) {
        if (level.getBlockEntity(from) instanceof ZiplinePoleBlockEntity fromPole) {
            fromPole.removeConnection(to);
        }
        if (level.getBlockEntity(to) instanceof ZiplinePoleBlockEntity toPole) {
            toPole.removeConnection(from);
        }
    }

    public static void setSlack(Level level, BlockPos from, BlockPos to, int slack) {
        if (level.getBlockEntity(from) instanceof ZiplinePoleBlockEntity fromPole) {
            fromPole.updateSlack(to, slack);
        }
        if (level.getBlockEntity(to) instanceof ZiplinePoleBlockEntity toPole) {
            toPole.updateSlack(from, slack);
        }
    }

    public List<ZiplineConnection> getConnections() {
        return connectionsView;
    }

    public @Nullable ZiplineConnection getConnection(BlockPos target) {
        for (ZiplineConnection connection : connections) {
            if (connection.target().equals(target)) {
                return connection;
            }
        }
        return null;
    }

    public boolean isFull() {
        return connections.size() >= MAX_CONNECTIONS;
    }

    public AABB getRenderBounds() {
        AABB bounds = new AABB(worldPosition).expandTowards(0.0, 1.0, 0.0);
        for (ZiplineConnection connection : connections) {
            bounds = bounds.minmax(new ZiplineSegment(worldPosition, connection.target(), connection.slack()).bounds());
        }
        return bounds;
    }

    private void addConnection(ZiplineConnection connection) {
        connections.add(connection);
        markUpdated();
    }

    private void removeConnection(BlockPos target) {
        if (connections.removeIf(connection -> connection.target().equals(target))) {
            markUpdated();
        }
    }

    private void updateSlack(BlockPos target, int slack) {
        connections.replaceAll(connection -> connection.target().equals(target) ? connection.withSlack(slack) : connection);
        markUpdated();
    }

    private void markUpdated() {
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level != null) {
            ZiplineIndex.get(level).add(this);
        }
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (level != null) {
            ZiplineIndex.get(level).remove(this);
        }
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        super.preRemoveSideEffects(pos, state);
        if (level != null && !level.isClientSide()) {
            for (ZiplineConnection connection : List.copyOf(connections)) {
                if (level.getBlockEntity(connection.target()) instanceof ZiplinePoleBlockEntity other) {
                    other.removeConnection(pos);
                }
            }
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.store(TAG_CONNECTIONS, ZiplineConnection.CODEC.listOf(), connections);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        connections.clear();
        input.read(TAG_CONNECTIONS, ZiplineConnection.CODEC.listOf()).ifPresent(connections::addAll);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveCustomOnly(registries);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
