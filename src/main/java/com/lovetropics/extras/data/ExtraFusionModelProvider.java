package com.lovetropics.extras.data;

import com.lovetropics.extras.ExtraBlocks;
import com.lovetropics.extras.LTExtras;
import com.supermartijn642.fusion.api.model.DefaultModelTypes;
import com.supermartijn642.fusion.api.model.ModelInstance;
import com.supermartijn642.fusion.api.model.data.ConnectingModelData;
import com.supermartijn642.fusion.api.model.data.ConnectingModelDataBuilder;
import com.supermartijn642.fusion.api.predicate.DefaultConnectionPredicates;
import com.supermartijn642.fusion.api.provider.FusionModelProvider;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import javax.annotation.Nullable;

/**
 * LTExtras
 * FIRE EMOJI, FIRE EMOJI, FIRE EMOJI
 */
@EventBusSubscriber(modid = LTExtras.MODID, value = Dist.CLIENT)
public class ExtraFusionModelProvider extends FusionModelProvider {
    public ExtraFusionModelProvider(PackOutput output) {
        super(LTExtras.MODID, output);
    }

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent.Client event) {
        event.createProvider(ExtraFusionModelProvider::new);
    }

    @Override
    protected void generate() {
        createConnectedPiecedModel(ExtraBlocks.RED_SHIPPING_CONTAINER.get(), null);
        createConnectedPiecedModel(ExtraBlocks.BLUE_SHIPPING_CONTAINER.get(), null);
        createConnectedPiecedModel(ExtraBlocks.YELLOW_SHIPPING_CONTAINER.get(), null);

        createConnectedPiecedModel(ExtraBlocks.WAREHOUSE_ROAD.get(), null);

        createConnectedSimpleGroundLayerModel(ExtraBlocks.WAREHOUSE_PARKING_MARKING.get(), null);
    }

    protected void createConnectedPiecedModel(Block block, @Nullable String texId) {
        ResourceLocation texture = texId == null ? TextureMapping.getBlockTexture(block) : LTExtras.location("block/" + texId);
        ConnectingModelData modelData = ConnectingModelDataBuilder.builder()
                .parent(ResourceLocation.withDefaultNamespace("block/cube_all"))
                .texture("all", texture)
                .connection(DefaultConnectionPredicates.isSameBlock())
                .build();
        addModel(
                ModelLocationUtils.getModelLocation(block),
                ModelInstance.of(DefaultModelTypes.CONNECTING, modelData)
        );
    }

    protected void createConnectedSimpleGroundLayerModel(Block block, @Nullable String texId) {
        ResourceLocation texture = texId == null ? TextureMapping.getBlockTexture(block) : LTExtras.location("block/" + texId);
        ConnectingModelData modelData = ConnectingModelDataBuilder.builder()
                .parent(LTExtras.location("block/ground_plane"))
                .texture("up", texture)
                .connection(DefaultConnectionPredicates.isSameBlock())
                .build();
        addModel(
                ModelLocationUtils.getModelLocation(block),
                ModelInstance.of(DefaultModelTypes.CONNECTING, modelData)
        );
    }
}
