package com.lovetropics.extras.client.screen;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.resolver.ResolvedServerAddress;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.client.multiplayer.resolver.ServerNameResolver;
import net.minecraft.network.Connection;
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.ping.ClientboundPongResponsePacket;
import net.minecraft.network.protocol.status.ClientStatusPacketListener;
import net.minecraft.network.protocol.status.ClientboundStatusResponsePacket;
import net.minecraft.network.protocol.status.ServerboundStatusRequestPacket;
import net.minecraft.server.network.EventLoopGroupHolder;
import net.minecraft.util.Util;

import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class AutoJoinServerPinger {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final Duration INTERVAL = Duration.ofSeconds(5);
    private static final Duration TIMEOUT = Duration.ofSeconds(30);

    private static final Executor EXECUTOR = Util.backgroundExecutor();

    private final ServerAddress address;

    private @Nullable CompletableFuture<Boolean> pendingPing;
    private Instant lastPingTime = Instant.now();

    public AutoJoinServerPinger(String address) {
        this.address = ServerAddress.parseString(address);
    }

    public boolean tick() {
        Duration timeSinceLastPing = Duration.between(lastPingTime, Instant.now());
        if (pendingPing != null) {
            return tickPendingPing(pendingPing, timeSinceLastPing);
        }
        if (timeSinceLastPing.compareTo(INTERVAL) > 0) {
            pendingPing = sendPing().handle((result, throwable) -> {
                if (throwable != null) {
                    LOGGER.debug("Ping failed with exception", throwable);
                    return false;
                }
                return result;
            });
            lastPingTime = Instant.now();
        }
        return false;
    }

    private boolean tickPendingPing(CompletableFuture<Boolean> future, Duration timeSinceLastPing) {
        if (future.getNow(false)) {
            return true;
        } else if (future.isDone()) {
            pendingPing = null;
            lastPingTime = Instant.now();
        } else if (timeSinceLastPing.compareTo(TIMEOUT) > 0) {
            LOGGER.debug("Ping timed out, retrying");
            pendingPing = null;
        }
        return false;
    }

    private CompletableFuture<Boolean> sendPing() {
        return CompletableFuture.supplyAsync(
                () -> ServerNameResolver.DEFAULT.resolveAddress(address).map(ResolvedServerAddress::asInetSocketAddress),
                EXECUTOR
        ).thenComposeAsync(resolvedAddress -> {
            if (resolvedAddress.isEmpty()) {
                return CompletableFuture.completedFuture(false);
            }
            LOGGER.debug("Sending ping to server at {}", resolvedAddress.get());
            return doPing(resolvedAddress.get());
        }, EXECUTOR);
    }

    private CompletableFuture<Boolean> doPing(InetSocketAddress resolvedAddress) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        Connection connection = Connection.connectToServer(resolvedAddress,  EventLoopGroupHolder.remote(Minecraft.getInstance().options.useNativeTransport()), null);
        ClientStatusPacketListener listener = new ClientStatusPacketListener() {
            @Override
            public void handleStatusResponse(ClientboundStatusResponsePacket packet) {
                LOGGER.debug("Got successful ping response from server");
                future.complete(true);
                connection.disconnect(Component.translatable("multiplayer.status.finished"));
            }

            @Override
            public void handlePongResponse(ClientboundPongResponsePacket packet) {
            }

            @Override
            public void onDisconnect(DisconnectionDetails details) {
                // Won't trigger if it was already completed
                if (future.complete(false)) {
                    LOGGER.debug("Disconnected while pinging server: {}", details.reason().getString());
                }
            }

            @Override
            public boolean isAcceptingMessages() {
                return connection.isConnected();
            }
        };

        connection.initiateServerboundStatusConnection(address.getHost(), address.getPort(), listener);
        connection.send(ServerboundStatusRequestPacket.INSTANCE);

        return future;
    }
}
