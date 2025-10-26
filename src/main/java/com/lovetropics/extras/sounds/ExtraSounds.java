package com.lovetropics.extras.sounds;

import com.lovetropics.extras.LTExtras;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ExtraSounds {
    public static final DeferredRegister<SoundEvent> REGISTER = DeferredRegister.create(Registries.SOUND_EVENT, LTExtras.MODID);

    public static final Holder<SoundEvent> HEELS_STEP = register("heels_step");

    private static Holder<SoundEvent> register(String name) {
        return REGISTER.register(name, () -> SoundEvent.createVariableRangeEvent(LTExtras.location(name)));
    }
}
