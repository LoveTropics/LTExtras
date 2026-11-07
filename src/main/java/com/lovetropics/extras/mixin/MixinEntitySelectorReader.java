package com.lovetropics.extras.mixin;

import com.lovetropics.extras.duck.ForceableArgument;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.advancements.predicates.MinMaxBounds;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

@Mixin(EntitySelectorParser.class)
public class MixinEntitySelectorReader implements ForceableArgument {
	@Shadow
	private int maxResults;
	@Shadow
	private MinMaxBounds.Doubles distance;
	@Shadow
	private Double deltaX;
	@Shadow
	private Double deltaY;
	@Shadow
	private Double deltaZ;
	@Unique
	private boolean ltextras$forced = false;

	@Unique
	private static final DynamicCommandExceptionType LIMIT_UNFORCED = new DynamicCommandExceptionType(
		count -> Component.translatableEscape("argument.entity.selector.limit.unforced", count)
	);

	@Override
	public void ltextras$setForced(boolean forced) {
		this.ltextras$forced = forced;
	}

	@Override
	public boolean ltextras$isForced() {
		return ltextras$forced;
	}

	@Inject(method = "parse", at = @At("RETURN"))
	private void ltextras$preventMistypedTags(CallbackInfoReturnable<EntitySelector> info) throws CommandSyntaxException {
		if (this.maxResults > 50
                && (this.distance == null || this.distance == MinMaxBounds.Doubles.ANY)
                && !ltextras$forced) {
			throw LIMIT_UNFORCED.create(this.maxResults);
		}
	}

    @Inject(method = "addPredicate", at = @At("HEAD"))
    private void ltextras$forceWithPredicate(Predicate<Entity> predicate, CallbackInfo ci) {
        // When @e/@a is called with a tag=,type=,etc assume the caller wants a tag that collects all entities.
        this.ltextras$forced = true;
    }
}
