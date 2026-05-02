package com.lovetropics.extras.client.entity.model;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.client.entity.state.SpinningSignRenderState;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.util.Mth;

public class SpinningSignModel extends EntityModel<SpinningSignRenderState> {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(LTExtras.id("spinning_sign"), "main");
    private final ModelPart root;
    private final ModelPart sign_panel;
    private final ModelPart sign_panel2;
    private final ModelPart sign_panel3;
    private final ModelPart sign_panel4;
    private final ModelPart angle_panels;
    private final ModelPart sign_panel5;
    private final ModelPart sign_panel6;
    private final ModelPart sign_panel7;
    private final ModelPart sign_panel8;

    public SpinningSignModel(ModelPart root) {
        super(root, RenderTypes::entityTranslucent);
        this.root = root.getChild("root");
        this.sign_panel = this.root.getChild("sign_panel");
        this.sign_panel2 = this.root.getChild("sign_panel2");
        this.sign_panel3 = this.root.getChild("sign_panel3");
        this.sign_panel4 = this.root.getChild("sign_panel4");
        this.angle_panels = this.root.getChild("angle_panels");
        this.sign_panel5 = this.angle_panels.getChild("sign_panel5");
        this.sign_panel6 = this.angle_panels.getChild("sign_panel6");
        this.sign_panel7 = this.angle_panels.getChild("sign_panel7");
        this.sign_panel8 = this.angle_panels.getChild("sign_panel8");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition sign_panel = root.addOrReplaceChild("sign_panel", CubeListBuilder.create().texOffs(0, 32).addBox(-32.0F, -32.0F, -80.0F, 64.0F, 32.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition sign_panel2 = root.addOrReplaceChild("sign_panel2", CubeListBuilder.create().texOffs(0, 32).addBox(-32.0F, -32.0F, -80.0F, 64.0F, 32.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        PartDefinition sign_panel3 = root.addOrReplaceChild("sign_panel3", CubeListBuilder.create().texOffs(0, 32).addBox(-32.0F, -32.0F, -80.0F, 64.0F, 32.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

        PartDefinition sign_panel4 = root.addOrReplaceChild("sign_panel4", CubeListBuilder.create().texOffs(0, 32).addBox(-32.0F, -32.0F, -80.0F, 64.0F, 32.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        PartDefinition angle_panels = root.addOrReplaceChild("angle_panels", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition sign_panel5 = angle_panels.addOrReplaceChild("sign_panel5", CubeListBuilder.create().texOffs(0, 0).addBox(-34.0F, -32.0F, -79.125F, 68.0F, 32.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

        PartDefinition sign_panel6 = angle_panels.addOrReplaceChild("sign_panel6", CubeListBuilder.create().texOffs(0, 0).addBox(-34.0F, -32.0F, -79.125F, 68.0F, 32.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.7854F, 0.0F));

        PartDefinition sign_panel7 = angle_panels.addOrReplaceChild("sign_panel7", CubeListBuilder.create().texOffs(0, 0).addBox(-34.0F, -32.0F, -79.125F, 68.0F, 32.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 2.3562F, 0.0F));

        PartDefinition sign_panel8 = angle_panels.addOrReplaceChild("sign_panel8", CubeListBuilder.create().texOffs(0, 0).addBox(-34.0F, -32.0F, -79.125F, 68.0F, 32.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -2.3562F, 0.0F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }



    @Override
    public void setupAnim(SpinningSignRenderState renderState) {
        super.setupAnim(renderState);
        renderState.yRot = Mth.clamp(renderState.ageInTicks % 360, -360, 360);
    }
}
