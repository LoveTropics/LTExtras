package com.lovetropics.extras.data;

import com.lovetropics.extras.ExtraBlocks;
import com.lovetropics.extras.LTExtras;
import com.supermartijn642.fusion.api.model.DefaultModelTypes;
import com.supermartijn642.fusion.api.model.ModelInstance;
import com.supermartijn642.fusion.api.model.data.ConnectingModelData;
import com.supermartijn642.fusion.api.model.data.ConnectingModelDataBuilder;
import com.supermartijn642.fusion.api.predicate.DefaultConnectionPredicates;
import com.supermartijn642.fusion.api.provider.FusionModelProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import javax.annotation.Nullable;

/**
 * LTExtras
 * FIRE EMOJI, FIRE EMOJI, FIRE EMOJI
 */
public class GenericFusionModelProvider extends FusionModelProvider {

    public GenericFusionModelProvider(PackOutput output) {
        super(LTExtras.MODID, output);
    }

    public static void onGatherData(GatherDataEvent.Client event) {
        event.createProvider(GenericFusionModelProvider::new);
    }

    @Override
    protected void generate() {
        createConnectedPiecedModel(ExtraBlocks.RED_SHIPPING_CONTAINER.getRegisteredName(), null);
        createConnectedPiecedModel(ExtraBlocks.BLUE_SHIPPING_CONTAINER.getRegisteredName(), null);
        createConnectedPiecedModel(ExtraBlocks.YELLOW_SHIPPING_CONTAINER.getRegisteredName(), null);

        createConnectedPiecedModel(ExtraBlocks.WAREHOUSE_ROAD.getRegisteredName(), null);

    }

    protected void createConnectedPiecedModel(String blockId, @Nullable String texId) {
        ConnectingModelData modelData =  ConnectingModelDataBuilder.builder()
                .parent(ResourceLocation.withDefaultNamespace("block/cube_all"))
                .texture("all", ResourceLocation.fromNamespaceAndPath(LTExtras.MODID, "block/" + (texId == null ? blockId.replaceAll("^[^:]*:\\s*", "") : texId)))
                .connection(DefaultConnectionPredicates.isSameBlock())
                .build();
        this.addModel(
                ResourceLocation.fromNamespaceAndPath(LTExtras.MODID, "block/" + blockId.replaceAll("^[^:]*:\\s*", "")),
                ModelInstance.of(DefaultModelTypes.CONNECTING, modelData)
        );
    }
}
