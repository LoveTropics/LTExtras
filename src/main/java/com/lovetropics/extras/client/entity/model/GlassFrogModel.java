package com.lovetropics.extras.client.entity.model;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.client.entity.state.AmazonRiverDolphinRenderState;
import com.lovetropics.extras.client.entity.state.GlassFrogRenderState;
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
 * Model for the Glass Frog, made by TripleHeadedSheep!
 */
public class GlassFrogModel extends EntityModel<GlassFrogRenderState> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(LTExtras.location("glass_frog"), "main");

    private final ModelPart body_base;
    private final ModelPart leg_back_right_1;
    private final ModelPart leg_back_right_2;
    private final ModelPart leg_back_right_3;
    private final ModelPart foot_back_right;
    private final ModelPart leg_back_left_1;
    private final ModelPart leg_back_left_2;
    private final ModelPart leg_back_left_3;
    private final ModelPart foot_back_left;
    private final ModelPart body_torso;
    private final ModelPart leg_front_right_1;
    private final ModelPart leg_front_right_2;
    private final ModelPart foot_front_right;
    private final ModelPart leg_front_left_1;
    private final ModelPart leg_front_left_2;
    private final ModelPart foot_front_left;
    private final ModelPart head;
    private final ModelPart ribbit_bag;
    private final ModelPart eye_left;
    private final ModelPart eye_right;

    public GlassFrogModel(ModelPart root) {
        super(root);
        this.body_base = root.getChild("body_base");
        this.leg_back_right_1 = this.body_base.getChild("leg_back_right_1");
        this.leg_back_right_2 = this.leg_back_right_1.getChild("leg_back_right_2");
        this.leg_back_right_3 = this.leg_back_right_2.getChild("leg_back_right_3");
        this.foot_back_right = this.leg_back_right_3.getChild("foot_back_right");
        this.leg_back_left_1 = this.body_base.getChild("leg_back_left_1");
        this.leg_back_left_2 = this.leg_back_left_1.getChild("leg_back_left_2");
        this.leg_back_left_3 = this.leg_back_left_2.getChild("leg_back_left_3");
        this.foot_back_left = this.leg_back_left_3.getChild("foot_back_left");
        this.body_torso = this.body_base.getChild("body_torso");
        this.leg_front_right_1 = this.body_torso.getChild("leg_front_right_1");
        this.leg_front_right_2 = this.leg_front_right_1.getChild("leg_front_right_2");
        this.foot_front_right = this.leg_front_right_2.getChild("foot_front_right");
        this.leg_front_left_1 = this.body_torso.getChild("leg_front_left_1");
        this.leg_front_left_2 = this.leg_front_left_1.getChild("leg_front_left_2");
        this.foot_front_left = this.leg_front_left_2.getChild("foot_front_left");
        this.head = this.body_torso.getChild("head");
        this.ribbit_bag = this.head.getChild("ribbit_bag");
        this.eye_left = this.head.getChild("eye_left");
        this.eye_right = this.head.getChild("eye_right");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body_base = partdefinition.addOrReplaceChild("body_base", CubeListBuilder.create().texOffs(0, 11).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 23.3F, 3.0F, -0.2618F, 0.0F, 0.0F));

        PartDefinition leg_back_right_1 = body_base.addOrReplaceChild("leg_back_right_1", CubeListBuilder.create().texOffs(8, 7).addBox(-2.0F, 0.0F, -0.75F, 2.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, 0.0F, 0.25F, 0.0436F, -0.9599F, -0.3054F));

        PartDefinition leg_back_right_2 = leg_back_right_1.addOrReplaceChild("leg_back_right_2", CubeListBuilder.create().texOffs(8, 8).addBox(-2.0F, 0.001F, 0.0F, 2.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, 0.0F, 0.25F, 0.0F, 2.6616F, 0.0F));

        PartDefinition leg_back_right_3 = leg_back_right_2.addOrReplaceChild("leg_back_right_3", CubeListBuilder.create().texOffs(0, 9).addBox(0.0F, 0.002F, 0.0F, 2.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, 0.0F, 0.0F, 0.0678F, 0.4755F, 0.1473F));

        PartDefinition foot_back_right = leg_back_right_3.addOrReplaceChild("foot_back_right", CubeListBuilder.create().texOffs(12, 9).addBox(0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, 0.0F, 0.0F, -0.3681F, -0.3817F, 0.1513F));

        PartDefinition leg_back_left_1 = body_base.addOrReplaceChild("leg_back_left_1", CubeListBuilder.create().texOffs(6, 9).addBox(0.0F, 0.0F, -0.75F, 2.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, 0.0F, 0.25F, 0.0436F, 0.9599F, 0.3054F));

        PartDefinition leg_back_left_2 = leg_back_left_1.addOrReplaceChild("leg_back_left_2", CubeListBuilder.create().texOffs(0, 10).addBox(0.0F, 0.001F, 0.0F, 2.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, 0.0F, 0.25F, 0.0F, -2.6616F, 0.0F));

        PartDefinition leg_back_left_3 = leg_back_left_2.addOrReplaceChild("leg_back_left_3", CubeListBuilder.create().texOffs(6, 10).addBox(-2.0F, 0.002F, 0.0F, 2.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, 0.0F, 0.0F, 0.0678F, -0.4755F, -0.1473F));

        PartDefinition foot_back_left = leg_back_left_3.addOrReplaceChild("foot_back_left", CubeListBuilder.create().texOffs(12, 10).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, 0.0F, 0.0F, -0.3681F, 0.3817F, -0.1513F));

        PartDefinition body_torso = body_base.addOrReplaceChild("body_torso", CubeListBuilder.create().texOffs(8, 0).addBox(-1.0F, 0.0F, -2.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(-0.2F))
                .texOffs(0, 0).addBox(-1.0F, 0.0F, -2.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.5F, -0.5F, 0.1309F, 0.0F, 0.0F));

        PartDefinition leg_front_right_1 = body_torso.addOrReplaceChild("leg_front_right_1", CubeListBuilder.create().texOffs(12, 11).addBox(-1.0F, 0.0F, -1.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 0.75F, -1.5F, 0.2778F, -0.1394F, -0.2396F));

        PartDefinition leg_front_right_2 = leg_front_right_1.addOrReplaceChild("leg_front_right_2", CubeListBuilder.create().texOffs(8, 3).addBox(0.0F, 0.001F, -2.0F, 1.0F, 0.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 0.0F, 0.0F, 0.0436F, 0.4363F, 0.0F));

        PartDefinition foot_front_right = leg_front_right_2.addOrReplaceChild("foot_front_right", CubeListBuilder.create().texOffs(12, 12).addBox(0.0F, 0.0F, -1.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -2.0F, -0.3054F, -0.5236F, 0.3491F));

        PartDefinition leg_front_left_1 = body_torso.addOrReplaceChild("leg_front_left_1", CubeListBuilder.create().texOffs(0, 13).addBox(0.0F, 0.0F, -1.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 0.75F, -1.5F, 0.2778F, 0.1394F, 0.2396F));

        PartDefinition leg_front_left_2 = leg_front_left_1.addOrReplaceChild("leg_front_left_2", CubeListBuilder.create().texOffs(8, 5).addBox(-1.0F, 0.001F, -2.0F, 1.0F, 0.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 0.0F, 0.0F, 0.0436F, -0.4363F, 0.0F));

        PartDefinition foot_front_left = leg_front_left_2.addOrReplaceChild("foot_front_left", CubeListBuilder.create().texOffs(4, 13).addBox(-1.0F, 0.0F, -1.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -2.0F, -0.3054F, 0.5236F, -0.3491F));

        PartDefinition head = body_torso.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 3).addBox(-1.0F, 0.0F, -2.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, 0.0F, -2.0F, 0.1309F, 0.0F, 0.0F));

        PartDefinition ribbit_bag = head.addOrReplaceChild("ribbit_bag", CubeListBuilder.create().texOffs(0, 6).addBox(-1.0F, -0.95F, -2.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(-0.01F)), PartPose.offset(0.0F, 0.95F, 0.0F));

        PartDefinition eye_left = head.addOrReplaceChild("eye_left", CubeListBuilder.create().texOffs(4, 11).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 0.0F, -1.05F, -1.2673F, 0.7565F, -0.9786F));

        PartDefinition eye_right = head.addOrReplaceChild("eye_right", CubeListBuilder.create().texOffs(8, 11).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 0.0F, -1.05F, -1.2673F, -0.7565F, 0.9786F));

        return LayerDefinition.create(meshdefinition, 16, 16);
    }

    public void setupAnim(GlassFrogRenderState state) {
        super.setupAnim(state);
        this.root.xRot = state.xRot * (float) (Math.PI / 180.0);
        this.root.yRot = state.yRot * (float) (Math.PI / 180.0);
    }
}
