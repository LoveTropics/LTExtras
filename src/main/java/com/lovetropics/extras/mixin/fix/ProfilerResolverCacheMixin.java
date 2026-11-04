package com.lovetropics.extras.mixin.fix;

import com.google.common.cache.LoadingCache;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.lovetropics.extras.duck.ExtendedProfileCache;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.players.ProfileResolver;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Mixin(ProfileResolver.Cached.class)
public class ProfilerResolverCacheMixin implements ExtendedProfileCache {
    @Shadow
    @Final
    private LoadingCache<UUID, Optional<GameProfile>> profileCacheById;
    @Shadow
    @Final
    private LoadingCache<String, Optional<GameProfile>> profileCacheByName;

    @WrapOperation(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/time/Duration;ofMinutes(J)Ljava/time/Duration;"))
    private Duration createDuration(long minutes, Operation<Duration> original) {
        // Not much point to invalidate - if a player joins, we'll update it
        return Duration.ofHours(2);
    }

    @Override
    public void lt$insertFromJoin(GameProfile profile) {
        profileCacheById.put(profile.id(), Optional.of(profile));
        profileCacheByName.put(profile.name(), Optional.of(profile));
    }
}
