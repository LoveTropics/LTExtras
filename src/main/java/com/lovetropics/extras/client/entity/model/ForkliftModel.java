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

public class ForkliftModel extends EntityModel<ForkliftRenderState> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(LTExtras.location("forklift"), "main");
    private final ModelPart forks;
    private final ModelPart wheelFrontRight;
    private final ModelPart wheelFrontLeft;
    private final ModelPart wheelBackRight;
    private final ModelPart wheelBackLeft;
    private final ModelPart steeringWheel;

    public ForkliftModel(ModelPart root) {
        super(root);
        this.forks = root.getChild("forks");

        wheelFrontRight = root.getChild("front_right");
        wheelFrontLeft = root.getChild("front_left");
        wheelBackRight = root.getChild("back_right");
        wheelBackLeft = root.getChild("back_left");
        steeringWheel = root.getChild("group").getChild("steering_wheel");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition forks = partdefinition.addOrReplaceChild("forks", CubeListBuilder.create().texOffs(0, 29).addBox(-3.0F, 5.0F, -8.5F, 2.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(30, 74).addBox(-3.0F, -1.0F, -1.5F, 2.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(30, 44).addBox(-12.0F, 5.0F, -8.5F, 2.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(48, 74).addBox(-12.0F, -1.0F, -1.5F, 2.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(30, 40).addBox(-14.0F, 4.0F, -0.5F, 15.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(68, 79).addBox(0.0F, 1.0F, -0.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(72, 79).addBox(-14.0F, 1.0F, -0.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(30, 42).addBox(-14.0F, 0.0F, -0.5F, 15.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 40).addBox(-14.0F, -5.0F, 0.0F, 15.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(6.5F, 18.0F, -13.5F));

        PartDefinition back_right = partdefinition.addOrReplaceChild("back_right", CubeListBuilder.create().texOffs(72, 0).addBox(-0.79F, -2.45F, -0.9555F, 2.0F, 5.0F, 2.0711F, new CubeDeformation(0.0F))
                .texOffs(58, 0).addBox(-0.79F, -0.9855F, -2.42F, 2.0F, 2.0711F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(20, 29).addBox(-0.84F, -2.375F, -2.495F, 0.0F, 4.35F, 4.35F, new CubeDeformation(0.0F)), PartPose.offset(-4.86F, 21.6F, 7.22F));

        PartDefinition octagon_r1 = back_right.addOrReplaceChild("octagon_r1", CubeListBuilder.create().texOffs(0, 85).addBox(-2.598F, -1.0335F, -2.498F, 1.996F, 2.0671F, 4.996F, new CubeDeformation(0.0F))
                .texOffs(0, 76).addBox(-2.598F, -2.498F, -1.0335F, 1.996F, 4.996F, 2.0671F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.81F, 0.05F, 0.08F, 0.7854F, 0.0F, 0.0F));

        PartDefinition back_left = partdefinition.addOrReplaceChild("back_left", CubeListBuilder.create().texOffs(72, 26).addBox(-1.21F, -2.45F, -1.0855F, 2.0F, 5.0F, 2.0711F, new CubeDeformation(0.0F))
                .texOffs(44, 59).addBox(-1.21F, -0.9855F, -2.55F, 2.0F, 2.0711F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(21, 29).addBox(0.84F, -2.375F, -1.975F, 0.0F, 4.35F, 4.35F, new CubeDeformation(0.0F)), PartPose.offset(4.86F, 21.6F, 7.35F));

        PartDefinition octagon_r2 = back_left.addOrReplaceChild("octagon_r2", CubeListBuilder.create().texOffs(0, 85).addBox(0.602F, -1.0335F, -2.498F, 1.996F, 2.0671F, 4.996F, new CubeDeformation(0.0F))
                .texOffs(0, 76).addBox(0.602F, -2.498F, -1.0335F, 1.996F, 4.996F, 2.0671F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.81F, 0.05F, -0.05F, 0.7854F, 0.0F, 0.0F));

        PartDefinition front_right = partdefinition.addOrReplaceChild("front_right", CubeListBuilder.create().texOffs(72, 57).addBox(-0.79F, -2.54F, -0.9605F, 2.0F, 5.0F, 2.0711F, new CubeDeformation(0.0F))
                .texOffs(58, 59).addBox(-0.79F, -1.0755F, -2.425F, 2.0F, 2.0711F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(22, 72).addBox(-0.84F, -2.015F, -2.475F, 0.0F, 4.35F, 4.35F, new CubeDeformation(0.0F)), PartPose.offset(-4.86F, 21.69F, -6.875F));

        PartDefinition octagon_r3 = front_right.addOrReplaceChild("octagon_r3", CubeListBuilder.create().texOffs(0, 85).addBox(-2.598F, -1.0335F, -2.498F, 1.996F, 2.0671F, 4.996F, new CubeDeformation(0.0F))
                .texOffs(0, 76).addBox(-2.598F, -2.498F, -1.0335F, 1.996F, 4.996F, 2.0671F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.81F, -0.04F, 0.075F, 0.7854F, 0.0F, 0.0F));

        PartDefinition front_left = partdefinition.addOrReplaceChild("front_left", CubeListBuilder.create().texOffs(66, 72).addBox(-1.21F, -2.45F, -1.0855F, 2.0F, 5.0F, 2.0711F, new CubeDeformation(0.0F))
                .texOffs(0, 61).addBox(-1.21F, -0.9855F, -2.55F, 2.0F, 2.0711F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(21, 29).addBox(0.84F, -2.375F, -1.975F, 0.0F, 4.35F, 4.35F, new CubeDeformation(0.0F)), PartPose.offset(4.86F, 21.6F, -6.75F));

        PartDefinition octagon_r4 = front_left.addOrReplaceChild("octagon_r4", CubeListBuilder.create().texOffs(0, 85).addBox(0.602F, -1.0335F, -2.498F, 1.996F, 2.0671F, 4.996F, new CubeDeformation(0.0F))
                .texOffs(0, 76).addBox(0.602F, -2.498F, -1.0335F, 1.996F, 4.996F, 2.0671F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.81F, 0.05F, -0.05F, 0.7854F, 0.0F, 0.0F));

        PartDefinition group = partdefinition.addOrReplaceChild("group", CubeListBuilder.create().texOffs(0, 55).addBox(-6.55F, -3.65F, 4.8F, 5.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(30, 57).addBox(-6.55F, -8.65F, 8.8F, 5.0F, 7.0F, 2.0F, new CubeDeformation(0.01F))
                .texOffs(18, 45).addBox(-0.55F, -17.65F, -6.2F, 2.0F, 20.0F, 1.5F, new CubeDeformation(0.0F))
                .texOffs(23, 45).addBox(-9.55F, -17.65F, -6.2F, 2.0F, 20.0F, 1.5F, new CubeDeformation(0.0F))
                .texOffs(58, 35).addBox(-7.55F, -8.65F, -5.2F, 7.0F, 2.0F, 0.5F, new CubeDeformation(0.0F))
                .texOffs(14, 61).addBox(-5.55F, -14.65F, -6.2F, 1.0F, 16.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(58, 7).addBox(-7.55F, -15.65F, -6.2F, 7.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(18, 66).addBox(-3.55F, -14.65F, -6.2F, 1.0F, 16.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 15).addBox(-7.55F, -4.65F, -4.2F, 7.0F, 7.0F, 7.0F, new CubeDeformation(0.0F))
                .texOffs(56, 12).addBox(-6.55F, -6.65F, -1.2F, 5.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-7.55F, -5.65F, 10.8F, 7.0F, 8.0F, 7.0F, new CubeDeformation(0.0F))
                .texOffs(0, 38).addBox(-9.55F, -3.65F, 17.8F, 11.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 45).addBox(-0.55F, -5.65F, 10.8F, 2.0F, 3.0F, 7.0F, new CubeDeformation(0.0F))
                .texOffs(50, 44).addBox(-9.55F, -5.65F, 10.8F, 2.0F, 3.0F, 7.0F, new CubeDeformation(0.0F))
                .texOffs(28, 0).addBox(-7.55F, -1.65F, 2.8F, 7.0F, 4.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(56, 18).addBox(-0.55F, -4.65F, -3.2F, 2.0F, 2.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(36, 74).addBox(0.45F, -13.65F, 14.8F, 1.0F, 8.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(54, 74).addBox(-9.55F, -13.65F, 14.8F, 1.0F, 8.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(78, 52).addBox(1.45F, -13.65F, 15.3F, 2.0F, 5.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(64, 79).addBox(-11.55F, -13.65F, 15.3F, 2.0F, 5.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(28, 12).addBox(-9.55F, -14.65F, 2.8F, 1.0F, 1.0F, 13.0F, new CubeDeformation(0.0F))
                .texOffs(30, 54).addBox(-8.55F, -14.65F, 13.8F, 9.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(56, 34).addBox(-8.55F, -14.65F, 5.8F, 9.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(56, 36).addBox(-8.55F, -14.65F, 7.8F, 9.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(56, 38).addBox(-8.55F, -14.65F, 9.8F, 9.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(44, 57).addBox(-8.55F, -14.65F, 11.8F, 9.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(52, 54).addBox(-8.55F, -14.65F, 2.8F, 9.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(28, 26).addBox(0.45F, -14.65F, 2.8F, 1.0F, 1.0F, 13.0F, new CubeDeformation(0.0F))
                .texOffs(58, 9).addBox(-0.15F, -11.65F, 1.3F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(62, 40).addBox(-0.15F, -11.65F, 1.3F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(64, 9).addBox(-9.95F, -11.65F, 1.3F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(56, 26).addBox(-9.55F, -4.65F, -3.2F, 2.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(4.05F, 21.65F, -6.8F));

        PartDefinition cube_r1 = group.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(67, 87).addBox(-1.0F, -3.0F, 0.0F, 2.0F, 6.0F, 1.0F, new CubeDeformation(-0.01F))
                .texOffs(58, 87).addBox(8.0F, -3.0F, 0.0F, 2.0F, 6.0F, 1.0F, new CubeDeformation(-0.01F)), PartPose.offsetAndRotation(-8.55F, -1.4957F, 3.0242F, 0.3927F, 0.0F, 0.0F));

        PartDefinition cube_r2 = group.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(44, 72).addBox(-5.0F, -5.5F, -0.75F, 1.0F, 11.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(40, 72).addBox(4.0F, -5.5F, -0.75F, 1.0F, 11.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(8, 68).addBox(4.0F, -5.5F, -0.75F, 1.0F, 11.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.05F, -9.15F, 2.05F, -0.3927F, 0.0F, 0.0F));

        PartDefinition cube_r3 = group.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(74, 12).addBox(-0.5F, -1.0F, -3.5F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.05F, -6.15F, 4.05F, 0.7854F, 0.0F, 0.0F));

        PartDefinition cube_r4 = group.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(15, 68).addBox(-0.75F, -4.5F, -0.75F, 1.5F, 6.5F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(15, 68).addBox(4.75F, -4.5F, -0.75F, 1.5F, 6.5F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.8F, -5.25F, -1.75F, 0.7854F, 0.0F, 0.0F));

        PartDefinition steering_wheel = group.addOrReplaceChild("steering_wheel", CubeListBuilder.create(), PartPose.offsetAndRotation(-4.05F, -6.9117F, 4.0762F, -0.7854F, 0.0F, 0.0F));

        PartDefinition cube_r5 = steering_wheel.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(74, 7).addBox(-2.5F, -2.5F, 0.0F, 5.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.5708F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
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
