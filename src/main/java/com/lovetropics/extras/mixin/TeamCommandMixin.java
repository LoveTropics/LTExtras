package com.lovetropics.extras.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.CommandSource;
import net.minecraft.command.impl.TeamCommand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentUtils;

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
	private static ITextComponent updateComponent(ITextComponent component, CommandSource source) throws CommandSyntaxException {
		return TextComponentUtils.updateForEntity(source, component, null, 0);
	}
}
