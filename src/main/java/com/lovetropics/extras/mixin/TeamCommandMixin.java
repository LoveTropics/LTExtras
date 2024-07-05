package com.lovetropics.extras.mixin;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.server.commands.TeamCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(TeamCommand.class)
public class TeamCommandMixin {

	/*
	 * Vanilla does not resolve text components when setting the prefix/suffix. This
	 * means that scoreboard values can't be used as prefix/suffix.
	 * 
	 * We simply add a call to the method that resolves text components (with a null
	 * entity because we don't have the player -- this runs for the whole team) and
	 * use that result component to set the team prefix/suffix.
	 * 
	 * The component will not be resolved after this, so to update it the /team
	 * modify command must be used again.
	 */
	@ModifyVariable(at = @At("HEAD"), method = { "setPrefix", "setSuffix" }, argsOnly = true)
	private static Component updateComponent(Component component, CommandSourceStack source) throws CommandSyntaxException {
		return ComponentUtils.updateForEntity(source, component, null, 0);
	}
}
