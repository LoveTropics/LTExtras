package com.lovetropics.extras.client.entity.model;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.client.entity.state.ForkliftRenderState;
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
import net.minecraft.world.entity.Entity;

public class ForkliftModel<T extends Entity> extends EntityModel<ForkliftRenderState> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(LTExtras.location("forklift"), "main");
    private final ModelPart cab;
    private final ModelPart forks;
    private final ModelPart bb_main;
    private final ModelPart wheelFrontLeft;
    private final ModelPart wheelBackRight;
    private final ModelPart wheelBackLeft;
    private final ModelPart wheelFrontRight;

    public ForkliftModel(ModelPart root) {
        super(root);
        this.cab = root.getChild("cab");
        this.forks = root.getChild("forks");
        this.bb_main = root.getChild("bb_main");

        wheelFrontRight = root.getChild("front_right");
        wheelFrontLeft = root.getChild("front_left");
        wheelBackRight = root.getChild("back_right");
        wheelBackLeft = root.getChild("back_left");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition front_left = root.addOrReplaceChild("front_left", CubeListBuilder.create().texOffs(32, 89).addBox(-1.0F, -2.5F, -2.5F, 2.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(9.0F, 21.5F, -11.5F));

        PartDefinition back_right = root.addOrReplaceChild("back_right", CubeListBuilder.create().texOffs(90, 90).addBox(-1.0F, -2.5F, -2.5F, 2.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-9.0F, 21.5F, 11.5F));

        PartDefinition back_left = root.addOrReplaceChild("back_left", CubeListBuilder.create().texOffs(46, 89).addBox(-1.0F, -2.5F, -2.5F, 2.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(9.0F, 21.5F, 11.5F));

        PartDefinition front_right = root.addOrReplaceChild("front_right", CubeListBuilder.create().texOffs(76, 90).addBox(-1.0F, -2.5F, -2.5F, 2.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-9.0F, 21.5F, -11.5F));

        PartDefinition cab = root.addOrReplaceChild("cab", CubeListBuilder.create().texOffs(56, 38).addBox(-1.0F, -19.0F, -27.0F, 2.0F, 2.0F, 26.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-15.0F, 0.0F, -29.0F, 16.0F, 8.0F, 30.0F, new CubeDeformation(0.0F))
                .texOffs(60, 66).addBox(-11.0F, -2.0F, -21.0F, 8.0F, 2.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(76, 76).addBox(-11.0F, -11.0F, -13.0F, 8.0F, 11.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 88).addBox(-1.0F, -19.0F, -29.0F, 2.0F, 19.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(8, 88).addBox(-15.0F, -19.0F, -29.0F, 2.0F, 19.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(16, 88).addBox(-1.0F, -19.0F, -1.0F, 2.0F, 19.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(28, 81).addBox(-13.0F, -19.0F, -1.0F, 12.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(28, 85).addBox(-13.0F, -19.0F, -29.0F, 12.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(24, 89).addBox(-15.0F, -19.0F, -1.0F, 2.0F, 19.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(0, 38).addBox(-15.0F, -19.0F, -27.0F, 2.0F, 2.0F, 26.0F, new CubeDeformation(0.0F)), PartPose.offset(7.0F, 14.0F, 14.0F));

        root.addOrReplaceChild("forks", CubeListBuilder.create().texOffs(0, 81).addBox(-3.0F, -3.0F, -1.0F, 12.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(0, 66).addBox(-3.0F, 0.0F, -14.0F, 2.0F, 2.0F, 13.0F, new CubeDeformation(0.0F))
                .texOffs(30, 66).addBox(7.0F, 0.0F, -14.0F, 2.0F, 2.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, 19.0F, -18.0F));

        PartDefinition bb_main = root.addOrReplaceChild("bb_main", CubeListBuilder.create().texOffs(60, 76).addBox(4.0F, -32.0F, -17.0F, 2.0F, 30.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(68, 76).addBox(-6.0F, -32.0F, -17.0F, 2.0F, 30.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public void setupAnim(ForkliftRenderState state) {
        super.setupAnim(state);

        wheelBackLeft.xRot = state.wheelRot;
        wheelBackRight.xRot = state.wheelRot;
        wheelFrontRight.xRot = state.wheelRot;
        wheelFrontLeft.xRot = state.wheelRot;

        forks.y = state.forkHeight;
    }
}
