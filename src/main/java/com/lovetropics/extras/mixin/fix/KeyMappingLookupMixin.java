package com.lovetropics.extras.mixin.fix;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyMappingLookup;
import net.minecraftforge.client.settings.KeyModifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.*;

@Mixin(value = KeyMappingLookup.class, remap = false)
public class KeyMappingLookupMixin {
    @Shadow(remap = false)
    @Final
    private static EnumMap<KeyModifier, Map<InputConstants.Key, Collection<KeyMapping>>> map;

    /**
     * @author Su5eD
     * @reason https://github.com/neoforged/NeoForge/pull/171
     */
    @Overwrite(remap = false)
    public List<KeyMapping> getAll(final InputConstants.Key keyCode) {
        final List<KeyMapping> matchingBindings = new ArrayList<>();
        final KeyModifier activeModifier = KeyModifier.getActiveModifier();
        // Apply active modifier only if the pressed key is not the modifier itself
        // Otherwise, look for key bindings without modifiers
        if (activeModifier == KeyModifier.NONE || activeModifier.matches(keyCode) || !matchingBindings.addAll(findKeybinds(keyCode, activeModifier))) {
            matchingBindings.addAll(findKeybinds(keyCode, KeyModifier.NONE));
        }
        return matchingBindings;
    }

    private List<KeyMapping> findKeybinds(final InputConstants.Key keyCode, final KeyModifier modifier) {
        final Collection<KeyMapping> modifierBindings = map.get(modifier).get(keyCode);
        if (modifierBindings != null) {
            return modifierBindings.stream().filter(binding -> binding.isActiveAndMatches(keyCode)).toList();
        }
        return List.of();
    }
}
