package com.lovetropics.extras.client.entity.model;

import com.lovetropics.extras.LTExtras;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartNames;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.util.Mth;

public class HighHeelsModel extends HumanoidModel<HumanoidRenderState> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(LTExtras.location("high_heels"), "main");

    public HighHeelsModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0f);
        PartDefinition root = mesh.getRoot();
        PartDefinition head = root.clearChild(PartNames.HEAD);
        head.clearChild(PartNames.HAT);
        root.clearChild(PartNames.BODY);
        root.clearChild(PartNames.LEFT_ARM);
        root.clearChild(PartNames.RIGHT_ARM);

        PartDefinition leftLeg = root.clearChild(PartNames.LEFT_LEG);
        PartDefinition rightLeg = root.clearChild(PartNames.RIGHT_LEG);

        CubeListBuilder heel = CubeListBuilder.create()
                .texOffs(0, 0)
                .addBox(-2.0f, 10.0f, -3.0f, 4.0f, 3.0f, 4.0f)
                .texOffs(0, 7).addBox(-2.0f, 10.0f, 1.0f, 4.0f, 3.0f, 4.0f)
                .texOffs(16, 0).addBox(-2.0f, 13.0f, 2.0f, 4.0f, 3.0f, 3.0f)
                .texOffs(16, 11).addBox(-2.0f, 13.0f, 5.0f, 4.0f, 3.0f, 2.0f)
                .texOffs(0, 14).addBox(-2.0f, 10.0f, -3.0f, 4.0f, 2.0f, 4.0f, new CubeDeformation(0.15f))
                .texOffs(16, 6).addBox(-2.0f, 13.0f, -3.0f, 4.0f, 3.0f, 2.0f);
        PartPose heelPose = PartPose.rotation(0.0f, Mth.PI, 0.0f);

        leftLeg.addOrReplaceChild("heel_left", heel, heelPose);
        rightLeg.addOrReplaceChild("heel_right", heel, heelPose);

        return LayerDefinition.create(mesh, 32, 32);
    }
}
