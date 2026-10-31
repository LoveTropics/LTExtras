package com.lovetropics.extras.client.entity.model;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.client.entity.state.SwingRenderState;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

public class SwingModel extends EntityModel<SwingRenderState> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(LTExtras.id("swing"), "main");

    private final ModelPart swingBit;

    public SwingModel(ModelPart root) {
        super(root);
        this.swingBit = root.getChild("swing-bit");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        partdefinition.addOrReplaceChild("swing-bit", CubeListBuilder.create().texOffs(0, 8).addBox(-7.0F, 37.0F, -4.5F, 14.0F, 2.0F, 9.0F, new CubeDeformation(0.0F))
                .texOffs(0, 19).addBox(-7.0F, 0.0F, -1.25F, 1.0F, 37.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(6, 19).addBox(6.0F, 0.0F, -1.0F, 1.0F, 37.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -15.0F, 0.25F));

        partdefinition.addOrReplaceChild("bb_main", CubeListBuilder.create().texOffs(0, 0).addBox(-25.0F, -41.0F, -2.75F, 50.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(SwingRenderState state) {
        super.setupAnim(state);
        swingBit.xRot = -Mth.lerp(state.partialTick, state.prevSwingAngle, state.swingAngle);
    }
}
