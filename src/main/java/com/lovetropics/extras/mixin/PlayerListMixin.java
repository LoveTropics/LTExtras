package com.lovetropics.extras.mixin;

import com.lovetropics.extras.PlayerListAccess;
import net.minecraft.server.management.PlayerList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PlayerList.class)
public class PlayerListMixin implements PlayerListAccess {
	@Shadow
	@Final
	@Mutable
	protected int maxPlayers;

	@Override
	public void setMaxPlayers(int maxPlayers) {
		this.maxPlayers = maxPlayers;
	}
}
