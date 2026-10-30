package com.lovetropics.extras.item.loot;

import com.lovetropics.extras.ExtraDataComponents;
import com.lovetropics.extras.data.attachment.ExtraAttachments;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.item.component.UseCooldown;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * LTExtras
 * FIRE EMOJI, FIRE EMOJI, FIRE EMOJI
 */
public class ConfigurableCrossbowItem extends CrossbowItem {
    public static final Predicate<ItemStack> FIREWORK_ONLY = (itemstack) -> itemstack.is(Items.FIREWORK_ROCKET);

    private final Settings settings;

    public ConfigurableCrossbowItem(Properties properties, Settings settings) {
        super(properties);
        this.settings = settings;
    }

    public ConfigurableCrossbowItem(Properties properties) {
        super(properties);
        this.settings = Settings.DEFAULT;
    }

    @Override
    public Predicate<ItemStack> getSupportedHeldProjectiles() {
        return this.settings.heldSupportedProjectiles;
    }

    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return this.settings.allSupportedProjectiles;
    }

    protected double getDefaultGravity() {
        return 0.05;
    }

    @Override
    public void performShooting(Level level, LivingEntity shooter, InteractionHand hand, ItemStack weapon, float power, float uncertainty, @Nullable LivingEntity targetOverride) {
        List<ItemStack> projectiles = weapon.getOrDefault(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY).itemCopies();
        super.performShooting(level, shooter, hand, weapon, power, uncertainty, targetOverride);

        if (!projectiles.isEmpty() && !this.settings.consumesLoaded) {
            weapon.set(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.ofNonEmpty(projectiles));
        }

        if (shooter instanceof ServerPlayer serverPlayer && weapon.get(DataComponents.USE_COOLDOWN) instanceof UseCooldown useCooldown) {
            useCooldown.apply(weapon, serverPlayer);
        }
    }

    @Override
    protected Projectile createProjectile(Level level, LivingEntity shooter, ItemStack heldItem, ItemStack projectile, boolean isCrit) {
        // Modify the projectile before it reaches CrossbowItem#createProjectile() so we can apply the arrow projectile override data component which the mixin will pick up to affect gravity and damage.
        // Only do this if at least one of the overrides is set.
        if (ARROW_ONLY.test(projectile) && (this.settings.projectileDamageOverride.isPresent() || this.settings.projectileGravityOverride.isPresent())) {
            applyArrowOverride(projectile, this.settings);
        }

        Projectile result = super.createProjectile(level, shooter, heldItem, projectile, isCrit);
        if (this.settings.projectileGravityOverride.isPresent()) {
            result.setData(ExtraAttachments.ARROW_GRAVITY_OVERRIDE.get(), this.settings.projectileGravityOverride.get());
        }
        return result;
    }

    private static void applyArrowOverride(ItemStack itemStack, Settings settings) {
        itemStack.set(ExtraDataComponents.ARROW_PROJECTILE_OVERRIDE, new ArrowItemProjectileOverrideData(settings.projectileDamageOverride, settings.projectileGravityOverride));
    }

    public record Settings(
            boolean consumesLoaded,
            Optional<Double> projectileDamageOverride,
            Optional<Double> projectileGravityOverride,
            Predicate<ItemStack> allSupportedProjectiles,
            Predicate<ItemStack> heldSupportedProjectiles
    ) {
        public static final ConfigurableCrossbowItem.Settings DEFAULT = new Settings(true, Optional.empty(), Optional.empty(), CrossbowItem.ARROW_ONLY, CrossbowItem.ARROW_OR_FIREWORK);
    }
}
