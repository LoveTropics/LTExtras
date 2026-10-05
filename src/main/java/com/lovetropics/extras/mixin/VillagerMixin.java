package com.lovetropics.extras.mixin;

import com.lovetropics.extras.data.attachment.ExtraAttachments;
import com.lovetropics.extras.extension.CustomTradeExtension;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.item.trading.VillagerTrade;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(Villager.class)
public abstract class VillagerMixin extends AbstractVillager {

    public VillagerMixin(EntityType<? extends AbstractVillager> type, Level level) {
        super(type, level);
    }

    @Inject(method = "updateTrades", at = @At("HEAD"), cancellable = true)
    public void onUpdateTrades(ServerLevel level, CallbackInfo ci) {
        CustomTradeExtension tradeExtension = getExistingDataOrNull(ExtraAttachments.TRADE_OVERRIDE.get());
        if (tradeExtension == null) {
            return;
        }

        tradeExtension.trade().ifPresent(trade -> {
            offers = new MerchantOffers();
            LootContext lootContext = new LootContext.Builder(
                    new LootParams.Builder(level)
                            .withParameter(LootContextParams.ORIGIN, this.position())
                            .withParameter(LootContextParams.THIS_ENTITY, this)
                            .withParameter(LootContextParams.ADDITIONAL_COST_COMPONENT_ALLOWED, Unit.INSTANCE)
                            .create(LootContextParamSets.VILLAGER_TRADE)
            )
                    .create(Optional.empty());

            for (Holder<VillagerTrade> villagerTradeHolder : trade) {
                VillagerTrade value = villagerTradeHolder.value();
                MerchantOffer offer = value.getOffer(lootContext);
                if (offer != null) {
                    offers.add(offer);
                }
            }

            ci.cancel();
        });

    }
}
