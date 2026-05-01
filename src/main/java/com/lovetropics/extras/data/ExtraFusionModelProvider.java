// Todo 26.1 Port - Wait for Fusion Update / Switch to Another Mod
//package com.lovetropics.extras.data;
//
//import com.lovetropics.extras.ExtraBlocks;
//import com.lovetropics.extras.LTExtras;
//import com.supermartijn642.fusion.api.model.DefaultModelTypes;
//import com.supermartijn642.fusion.api.model.ModelInstance;
//import com.supermartijn642.fusion.api.model.ModelType;
//import com.supermartijn642.fusion.api.model.data.BaseModelData;
//import com.supermartijn642.fusion.api.model.data.ConnectingModelData;
//import com.supermartijn642.fusion.api.model.data.ConnectingModelDataBuilder;
//import com.supermartijn642.fusion.api.predicate.DefaultConnectionPredicates;
//import com.supermartijn642.fusion.api.provider.FusionModelProvider;
//import net.minecraft.client.data.models.model.ModelInstance;
//import net.minecraft.client.data.models.model.ModelLocationUtils;
//import net.minecraft.client.data.models.model.TextureMapping;
//import net.minecraft.client.renderer.block.model.BlockModel;
//import net.minecraft.data.PackOutput;
//import net.minecraft.resources.Identifier;
//import net.minecraft.world.level.block.Block;
//import net.neoforged.api.distmarker.Dist;
//import net.neoforged.bus.api.SubscribeEvent;
//import net.neoforged.fml.common.EventBusSubscriber;
//import net.neoforged.neoforge.data.event.GatherDataEvent;
//
//import javax.annotation.Nullable;
//
///**
// * LTExtras
// * FIRE EMOJI, FIRE EMOJI, FIRE EMOJI
// */
//@EventBusSubscriber(modid = LTExtras.MODID, value = Dist.CLIENT)
//public class ExtraFusionModelProvider extends FusionModelProvider {
//    public ExtraFusionModelProvider(PackOutput output) {
//        super(LTExtras.MODID, output);
//    }
//
//    @SubscribeEvent
//    public static void onGatherData(GatherDataEvent.Client event) {
//        event.createProvider(ExtraFusionModelProvider::new);
//    }
//
//    @Override
//    protected void generate() {
//        createConnectedPiecedModel(ExtraBlocks.RED_SHIPPING_CONTAINER.get(), null);
//        createConnectedPiecedModel(ExtraBlocks.BLUE_SHIPPING_CONTAINER.get(), null);
//        createConnectedPiecedModel(ExtraBlocks.YELLOW_SHIPPING_CONTAINER.get(), null);
//        createConnectedPiecedModel(ExtraBlocks.GREEN_SHIPPING_CONTAINER.get(), null);
//
//        createConnectedPiecedModel(ExtraBlocks.WAREHOUSE_ROAD.get(), null);
//
//        createGroundLayerModel(ExtraBlocks.WAREHOUSE_PARKING_MARKING.get(), DefaultModelTypes.CONNECTING, null);
//        //createGroundLayerModel(ExtraBlocks.WAREHOUSE_ROAD_BOOSTER.get(), DefaultModelTypes.BASE, null);
//
//        createBaseModel(ExtraBlocks.WAREHOUSE_FLOOR.get(), null, null);
//    }
//
//    protected void createConnectedPiecedModel(Block block, @Nullable String texId) {
//        Identifier texture = texId == null ? TextureMapping.getBlockTexture(block) : LTExtras.location("block/" + texId);
//        ConnectingModelData modelData = ConnectingModelDataBuilder.builder()
//                .parent(Identifier.withDefaultNamespace("block/cube_all"))
//                .texture("all", texture)
//                .connection(DefaultConnectionPredicates.isSameBlock())
//                .build();
//        addModel(
//                ModelLocationUtils.getModelLocation(block),
//                ModelInstance.of(DefaultModelTypes.CONNECTING, modelData)
//        );
//    }
//
//    protected void createBaseModel(Block block, @Nullable String texId, @Nullable Identifier modelParent) {
//        Identifier texture = texId == null ? TextureMapping.getBlockTexture(block) : LTExtras.location("block/" + texId);
//        ConnectingModelData modelData = ConnectingModelDataBuilder.builder()
//                .parent(modelParent == null ? Identifier.withDefaultNamespace("block/cube_all") : modelParent)
//                .texture("all", texture)
//                .connection(DefaultConnectionPredicates.isSameBlock())
//                .build();
//        addModel(
//                ModelLocationUtils.getModelLocation(block),
//                ModelInstance.of(DefaultModelTypes.BASE, modelData)
//        );
//    }
//
//    protected void createGroundLayerModel(Block block, @Nullable ModelType modelType, @Nullable String texId) {
//        Identifier texture = texId == null ? TextureMapping.getBlockTexture(block) : LTExtras.location("block/" + texId);
//        ConnectingModelData modelData = ConnectingModelDataBuilder.builder()
//                .parent(LTExtras.location("block/ground_plane"))
//                .texture("up", texture)
//                .connection(DefaultConnectionPredicates.isSameBlock())
//                .build();
//        addModel(
//                ModelLocationUtils.getModelLocation(block),
//                ModelInstance.of(modelType == null ? DefaultModelTypes.BASE : modelType, modelData)
//        );
//    }
//}
