package com.lovetropics.extras.client.entity.model;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.client.entity.state.AmazonRiverDolphinRenderState;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.MeshTransformer;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/**
 * Model for the Amazon River Dolphin, made by TripleHeadedSheep!
 */
public class AmazonRiverDolphinModel extends EntityModel<AmazonRiverDolphinRenderState> {
    public static final MeshTransformer BABY_TRANSFORMER = MeshTransformer.scaling(0.5F);

    public static final ModelLayerLocation LAYER = new ModelLayerLocation(LTExtras.id("amazon_river_dolphin"), "main");
    public static final ModelLayerLocation BABY_LAYER = new ModelLayerLocation(LTExtras.id("amazon_river_dolphin_baby"), "main");

    private final ModelPart chest;
    private final ModelPart head;
    private final ModelPart mouth;
    private final ModelPart mouthLower;
    private final ModelPart fin;
    private final ModelPart flipperRight;
    private final ModelPart flipperLeft;
    private final ModelPart tail1;
    private final ModelPart tail2;
    private final ModelPart tail3;
    private final ModelPart tail4;
    private final ModelPart tailfinRight;
    private final ModelPart tailfinLeft;

    public AmazonRiverDolphinModel(ModelPart root) {
        super(root);
        var bodyBase = root.getChild("body_base");
        this.chest = bodyBase.getChild("chest");
        this.head = this.chest.getChild("head_base");
        this.mouth = this.head.getChild("mouth_upper");
        this.mouthLower = this.mouth.getChild("mouth_lower");
        this.fin = bodyBase.getChild("fin");
        this.flipperRight = bodyBase.getChild("flipper_right");
        this.flipperLeft = bodyBase.getChild("flipper_left");
        this.tail1 = bodyBase.getChild("tail_1");
        this.tail2 = this.tail1.getChild("tail_2");
        this.tail3 = this.tail2.getChild("tail_3");
        this.tail4 = this.tail3.getChild("tail_4");
        this.tailfinRight = this.tail4.getChild("tailfin_right");
        this.tailfinLeft = this.tail4.getChild("tailfin_left");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition root = meshdefinition.getRoot();

        PartDefinition bodyBase = root.addOrReplaceChild("body_base", CubeListBuilder.create().texOffs(0, 0).addBox(-4.5F, -5.0F, -13.0F, 9.0F, 9.0F, 15.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 20.0F, 5.0F));

        PartDefinition flipperLeft = bodyBase.addOrReplaceChild("flipper_left", CubeListBuilder.create().texOffs(0, 51).addBox(-1.0F, -0.5F, -1.0F, 10.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.5F, 2.5F, -13.0F, 0.0F, -0.4363F, 0.1309F));
        flipperLeft.addOrReplaceChild("flipper_leftconn_r1", CubeListBuilder.create().texOffs(24, 24).addBox(0.0F, -0.5F, 0.0F, 9.0F, 1.0F, 3.0F, new CubeDeformation(-0.02F)), PartPose.offsetAndRotation(-1.0F, 0.0F, 3.0F, 0.0F, 0.3491F, 0.0F));

        PartDefinition fin = bodyBase.addOrReplaceChild("fin", CubeListBuilder.create().texOffs(44, 15).addBox(-0.5F, -2.0F, -9.0F, 1.0F, 2.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, 0.2182F, 0.0F, 0.0F));
        fin.addOrReplaceChild("fin_r1", CubeListBuilder.create().texOffs(0, 5).addBox(-0.5F, 0.0F, 0.0F, 1.0F, 2.0F, 4.0F, new CubeDeformation(-0.02F)), PartPose.offsetAndRotation(0.0F, -2.0F, 0.0F, -0.829F, 0.0F, 0.0F));

        PartDefinition tail1 = bodyBase.addOrReplaceChild("tail_1", CubeListBuilder.create().texOffs(0, 28).addBox(-3.5F, 0.0F, 0.0F, 7.0F, 7.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 2.0F, -0.3491F, 0.0F, 0.0F));
        tail1.addOrReplaceChild("tail_1conn_r1", CubeListBuilder.create().texOffs(40, 57).addBox(-3.5F, 0.0F, 0.0F, 7.0F, 2.0F, 5.0F, new CubeDeformation(-0.02F)), PartPose.offsetAndRotation(0.0F, 7.0F, 0.0F, 0.3491F, 0.0F, 0.0F));

        PartDefinition tail2 = tail1.addOrReplaceChild("tail_2", CubeListBuilder.create().texOffs(44, 2).addBox(-2.5F, 0.0F, 0.0F, 5.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 5.0F));
        tail2.addOrReplaceChild("tail_2conn_r1", CubeListBuilder.create().texOffs(41, 28).addBox(-2.5F, 0.0F, -1.0F, 5.0F, 2.0F, 6.0F, new CubeDeformation(-0.02F)), PartPose.offsetAndRotation(0.0F, 4.9F, 0.0F, 0.3491F, 0.0F, 0.0F));

        PartDefinition tail3 = tail2.addOrReplaceChild("tail_3", CubeListBuilder.create().texOffs(33, 7).addBox(-1.5F, 0.0F, -1.0F, 3.0F, 3.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 5.0F, 0.0873F, 0.0F, 0.0F));
        tail3.addOrReplaceChild("tail_3conn_r1", CubeListBuilder.create().texOffs(33, 0).addBox(-1.5F, -1.0F, 0.0F, 3.0F, 2.0F, 5.0F, new CubeDeformation(-0.02F)), PartPose.offsetAndRotation(0.0F, 4.0F, -1.0F, 0.3927F, 0.0F, 0.0F));

        PartDefinition tail4 = tail3.addOrReplaceChild("tail_4", CubeListBuilder.create().texOffs(8, 9).addBox(-0.5F, -3.0F, 0.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(0, 56).addBox(-5.0F, -2.0F, 1.25F, 10.0F, 1.0F, 2.0F, new CubeDeformation(-0.02F)), PartPose.offsetAndRotation(0.0F, 3.0F, 4.0F, 0.1745F, 0.0F, 0.0F));

        tail4.addOrReplaceChild("tailfin_left", CubeListBuilder.create().texOffs(0, 59).addBox(0.0F, -1.0F, 0.0F, 6.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -1.0F, -1.75F, 0.0F, -0.4363F, 0.0F));
        tail4.addOrReplaceChild("tailfin_right", CubeListBuilder.create().texOffs(14, 60).addBox(-6.0F, -1.0F, 0.0F, 6.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, -1.0F, -1.75F, 0.0F, 0.4363F, 0.0F));

        PartDefinition chest = bodyBase.addOrReplaceChild("chest", CubeListBuilder.create().texOffs(0, 40).addBox(-3.5F, 0.0F, -5.0F, 7.0F, 6.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, -13.0F, 0.0873F, 0.0F, 0.0F));
        chest.addOrReplaceChild("chest_r1", CubeListBuilder.create().texOffs(19, 34).addBox(-3.5F, -3.0F, -6.0F, 7.0F, 3.0F, 6.0F, new CubeDeformation(-0.02F)), PartPose.offsetAndRotation(0.0F, 8.5F, 0.0F, -0.3491F, 0.0F, 0.0F));

        PartDefinition headBase = chest.addOrReplaceChild("head_base", CubeListBuilder.create().texOffs(24, 43).addBox(-3.0F, -3.0F, -4.0F, 6.0F, 6.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 3.0F, -5.0F, -0.0873F, 0.0F, 0.0F));
        headBase.addOrReplaceChild("head_bulge_r1", CubeListBuilder.create().texOffs(41, 37).addBox(-2.0F, 0.0F, -1.0F, 4.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.25F, -3.75F, -0.1309F, 0.0F, 0.0F));

        PartDefinition mouthUpper = headBase.addOrReplaceChild("mouth_upper", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -2.0F, -2.0F, 3.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(29, 54).addBox(-1.0F, -2.0F, -8.0F, 2.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, -4.0F, 0.0873F, 0.0F, 0.0F));

        PartDefinition mouthLower = mouthUpper.addOrReplaceChild("mouth_lower", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.0436F, 0.0F, 0.0F));
        mouthLower.addOrReplaceChild("mouth_lower_r1", CubeListBuilder.create().texOffs(42, 47).addBox(-1.0F, 0.0F, -8.0F, 2.0F, 1.0F, 9.0F, new CubeDeformation(-0.02F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.3054F, 0.0F, 0.0F));

        PartDefinition flipperRight = bodyBase.addOrReplaceChild("flipper_right", CubeListBuilder.create().texOffs(19, 28).addBox(-9.0F, -0.5F, -1.0F, 10.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.5F, 2.5F, -13.0F, 0.0F, 0.4363F, -0.1309F));
        flipperRight.addOrReplaceChild("flipper_rightconn_r1", CubeListBuilder.create().texOffs(0, 24).addBox(-9.0F, -0.5F, 0.0F, 9.0F, 1.0F, 3.0F, new CubeDeformation(-0.02F)), PartPose.offsetAndRotation(1.0F, 0.0F, 3.0F, 0.0F, -0.3491F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    public void setupAnim(AmazonRiverDolphinRenderState state) {
        super.setupAnim(state);
        this.root.xRot = state.xRot * (float) (Math.PI / 180.0);
        this.root.yRot = state.yRot * (float) (Math.PI / 180.0);

        if (state.isMouthOpen) {
            this.mouthLower.xRot = -0.15F;
        } else {
            this.mouthLower.xRot = -0.3F;
        }

        if (state.isMoving) {
            this.root.xRot = this.root.xRot + (-0.05F - 0.05F * Mth.cos(state.ageInTicks * 0.3F));
            this.tail1.xRot = -0.1F * Mth.cos(state.ageInTicks * 0.3F);

            this.tailfinRight.xRot = -0.2F * Mth.cos(state.ageInTicks * 0.3F);
            this.tailfinLeft.xRot = -0.2F * Mth.cos(state.ageInTicks * 0.3F);

            this.flipperRight.yRot = -0.2F * Mth.cos(state.ageInTicks * 0.2F);
            this.flipperLeft.yRot = 0.2F * Mth.cos(state.ageInTicks * 0.2F);

            this.flipperRight.xRot = 0.1F * Mth.cos(state.ageInTicks * 0.6F);
            this.flipperLeft.xRot = 0.1F * Mth.cos(state.ageInTicks * 0.6F);
        }
    }
}
