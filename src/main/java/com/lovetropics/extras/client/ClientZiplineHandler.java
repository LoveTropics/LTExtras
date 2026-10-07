package com.lovetropics.extras.client;

import com.lovetropics.extras.ExtraItems;
import com.lovetropics.extras.network.message.ServerboundZiplineRidePacket;
import com.lovetropics.extras.network.message.ServerboundZiplineSlackPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.jspecify.annotations.Nullable;
import com.lovetropics.extras.zipline.ZiplineIndex;
import com.lovetropics.extras.zipline.ZiplinePoleBlock;
import com.lovetropics.extras.zipline.ZiplineRide;
import com.lovetropics.extras.zipline.ZiplineRider;
import com.lovetropics.extras.zipline.ZiplineSegment;

import java.util.Objects;
import java.util.Optional;

/**
 * Ropes are not blocks or entities, so clicks on them are picked up here. Also keeps the server informed of which rope
 * the local player is riding.
 */
@EventBusSubscriber(Dist.CLIENT)
public final class ClientZiplineHandler {
    private static final double ROPE_PICK_RADIUS = 0.3;
    private static final int REATTACH_COOLDOWN = 10;

    private static ServerboundZiplineRidePacket.@Nullable Segment sentSegment;
    private static int cooldown;

    @SubscribeEvent
    public static void onInteraction(InputEvent.InteractionKeyMappingTriggered event) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (!event.isUseItem() || player == null || ZiplineRider.isRiding(player)) {
            return;
        }

        boolean holdingWrench = player.isHolding(ExtraItems.ZIPLINE_WRENCH.get());
        ZiplineIndex index = ZiplineIndex.get(player.level());
        Vec3 lookVector = player.getViewVector(1.0f);
        HitResult hitResult = minecraft.hitResult;

        // Poles take priority over the ends of the ropes attached to them
        if (hitResult instanceof BlockHitResult blockHit && hitResult.getType() == HitResult.Type.BLOCK) {
            BlockState state = player.level().getBlockState(blockHit.getBlockPos());
            if (state.getBlock() instanceof ZiplinePoleBlock) {
                if (!holdingWrench && canAttach(player)) {
                    ZiplineSegment segment = index.pickSegmentFrom(ZiplinePoleBlock.getBasePos(blockHit.getBlockPos(), state), lookVector);
                    if (segment != null) {
                        attach(player, new ZiplineRide(segment, 0.0, player.getDeltaMovement().dot(segment.tangent(0.0)), 1));
                        consume(event);
                    }
                }
                return;
            }
        }

        Vec3 eyePosition = player.getEyePosition();
        double reach = player.blockInteractionRange();
        if (hitResult != null && hitResult.getType() != HitResult.Type.MISS) {
            reach = Math.min(reach, eyePosition.distanceTo(hitResult.getLocation()));
        }
        ZiplineSegment.@Nullable Hit ropeHit = index.raycast(eyePosition, eyePosition.add(lookVector.scale(reach)), ROPE_PICK_RADIUS);
        if (ropeHit == null) {
            return;
        }

        ZiplineSegment segment = ropeHit.segment();
        if (holdingWrench) {
            ClientPacketDistributor.sendToServer(new ServerboundZiplineSlackPacket(segment.from(), segment.to(), !player.isShiftKeyDown()));
            consume(event);
        } else if (canAttach(player)) {
            Vec3 tangent = segment.tangent(ropeHit.t());
            int direction = lookVector.dot(tangent) >= 0.0 ? 1 : -1;
            attach(player, new ZiplineRide(segment, ropeHit.t(), player.getDeltaMovement().dot(tangent), direction));
            consume(event);
        }
    }

    private static boolean canAttach(LocalPlayer player) {
        return cooldown <= 0 && !player.isShiftKeyDown() && !player.isPassenger() && !player.isSpectator();
    }

    private static void attach(LocalPlayer player, ZiplineRide ride) {
        ZiplineRider.startRiding(player, ride);
        // Send straight away so that the server knows before it receives the movement onto the rope
        sendSegment(toPacketSegment(ride));
    }

    private static void consume(InputEvent.InteractionKeyMappingTriggered event) {
        event.setCanceled(true);
        event.setSwingHand(true);
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            sentSegment = null;
            cooldown = 0;
            return;
        }
        if (cooldown > 0) {
            cooldown--;
        }

        ServerboundZiplineRidePacket.Segment segment = toPacketSegment(ZiplineRider.getRide(player));
        if (!Objects.equals(segment, sentSegment)) {
            if (segment == null) {
                cooldown = REATTACH_COOLDOWN;
            }
            sendSegment(segment);
        }
    }

    private static ServerboundZiplineRidePacket.@Nullable Segment toPacketSegment(@Nullable ZiplineRide ride) {
        return ride != null ? new ServerboundZiplineRidePacket.Segment(ride.segment().from(), ride.segment().to()) : null;
    }

    private static void sendSegment(ServerboundZiplineRidePacket.@Nullable Segment segment) {
        ClientPacketDistributor.sendToServer(new ServerboundZiplineRidePacket(Optional.ofNullable(segment)));
        sentSegment = segment;
    }
}
