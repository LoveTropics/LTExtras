package com.lovetropics.extras.client.entity.model;

import com.lovetropics.extras.LTExtras;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.Mth;

public class PhilippineWartyPigModel extends EntityModel<LivingEntityRenderState>  {
	public static final ModelLayerLocation LAYER = new ModelLayerLocation(LTExtras.id("lt_philippine_warty_pig"),  "main");
	private final ModelPart body_base;
	private final ModelPart tail;
	private final ModelPart head_main;
	private final ModelPart jaw_upper;
	private final ModelPart snout;
	private final ModelPart jaw_lower;
	private final ModelPart ear_left;
	private final ModelPart ear_right;
	private final ModelPart victorian_era_chops_left;
	private final ModelPart victorian_era_chops_right;
	private final ModelPart head_scruff_front;
	private final ModelPart head_scruff1;
	private final ModelPart head_scruff3;
	private final ModelPart head_scruff2;
	private final ModelPart leg_front_left;
	private final ModelPart leg_front_right;
	private final ModelPart leg_back_left;
	private final ModelPart leg_back_right;

	public PhilippineWartyPigModel(ModelPart root) {
        super(root);
		this.body_base = root.getChild("body_base");
		this.tail = this.body_base.getChild("tail");
		this.head_main = this.body_base.getChild("head_main");
		this.jaw_upper = this.head_main.getChild("jaw_upper");
		this.snout = this.jaw_upper.getChild("snout");
		this.jaw_lower = this.head_main.getChild("jaw_lower");
		this.ear_left = this.head_main.getChild("ear_left");
		this.ear_right = this.head_main.getChild("ear_right");
		this.victorian_era_chops_left = this.head_main.getChild("victorian_era_chops_left");
		this.victorian_era_chops_right = this.head_main.getChild("victorian_era_chops_right");
		this.head_scruff_front = this.head_main.getChild("head_scruff_front");
		this.head_scruff1 = this.head_main.getChild("head_scruff1");
		this.head_scruff3 = this.head_main.getChild("head_scruff3");
		this.head_scruff2 = this.head_main.getChild("head_scruff2");
		this.leg_front_left = this.body_base.getChild("leg_front_left");
		this.leg_front_right = this.body_base.getChild("leg_front_right");
		this.leg_back_left = this.body_base.getChild("leg_back_left");
		this.leg_back_right = this.body_base.getChild("leg_back_right");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body_base = partdefinition.addOrReplaceChild("body_base", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -1.0F, -10.0F, 6.0F, 7.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 13.0F, 5.0F));

		PartDefinition tail = body_base.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, 0.0F, 0.0F, 1.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, 2.0F, 0.1309F, 0.0F, 0.0F));

		PartDefinition head_main = body_base.addOrReplaceChild("head_main", CubeListBuilder.create().texOffs(0, 20).addBox(-2.5F, -3.0F, -3.0F, 5.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 2.0F, -10.0F));

		PartDefinition jaw_upper = head_main.addOrReplaceChild("jaw_upper", CubeListBuilder.create().texOffs(0, 29).addBox(-1.5F, 0.0F, -5.0F, 3.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(0, 36).addBox(-1.5F, 2.0F, -3.0F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.0F, -3.0F, 0.3927F, 0.0F, 0.0F));

		PartDefinition wart_right_r1 = jaw_upper.addOrReplaceChild("wart_right_r1", CubeListBuilder.create().texOffs(17, 34).addBox(-1.0F, 0.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, 0.0F, -2.0F, 0.0F, 0.0F, 0.2618F));

		PartDefinition wart_left_r1 = jaw_upper.addOrReplaceChild("wart_left_r1", CubeListBuilder.create().texOffs(17, 34).addBox(0.0F, 0.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, 0.0F, -2.0F, 0.0F, 0.0F, -0.2618F));

		PartDefinition snout = jaw_upper.addOrReplaceChild("snout", CubeListBuilder.create().texOffs(17, 30).addBox(-1.5F, 0.0F, -1.0F, 3.0F, 2.0F, 1.0F, new CubeDeformation(-0.01F)), PartPose.offsetAndRotation(0.0F, 0.0F, -4.0F, -0.3054F, 0.0F, 0.0F));

		PartDefinition jaw_lower = head_main.addOrReplaceChild("jaw_lower", CubeListBuilder.create().texOffs(0, 40).addBox(-1.5F, -2.0F, -4.0F, 3.0F, 2.0F, 4.0F, new CubeDeformation(0.01F)), PartPose.offset(0.0F, 3.0F, -3.0F));

		PartDefinition tooth_right_r1 = jaw_lower.addOrReplaceChild("tooth_right_r1", CubeListBuilder.create().texOffs(17, 37).addBox(0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.5F, -1.0F, -3.0F, -0.1291F, -0.0283F, -0.5642F));

		PartDefinition tooth_left_r1 = jaw_lower.addOrReplaceChild("tooth_left_r1", CubeListBuilder.create().texOffs(17, 37).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.5F, -1.0F, -3.0F, -0.1291F, 0.0283F, 0.5642F));

		PartDefinition ear_left = head_main.addOrReplaceChild("ear_left", CubeListBuilder.create().texOffs(17, 21).addBox(-1.0F, -2.0F, 0.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.5F, -0.5F, -1.0F, 0.2812F, 0.6053F, 0.0363F));

		PartDefinition ear_right = head_main.addOrReplaceChild("ear_right", CubeListBuilder.create().texOffs(17, 21).mirror().addBox(0.0F, -2.0F, 0.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-2.5F, -0.5F, -1.0F, 0.2812F, -0.6053F, -0.0363F));

		PartDefinition victorian_era_chops_left = head_main.addOrReplaceChild("victorian_era_chops_left", CubeListBuilder.create().texOffs(13, 40).addBox(0.0F, 0.0F, -4.0F, 2.0F, 0.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.5F, 1.0F, 0.0F, 0.3481F, 0.2602F, 0.4635F));

		PartDefinition victorian_era_chops_right = head_main.addOrReplaceChild("victorian_era_chops_right", CubeListBuilder.create().texOffs(13, 40).mirror().addBox(-2.0F, 0.0F, -4.0F, 2.0F, 0.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-2.5F, 1.0F, 0.0F, 0.3481F, -0.2602F, -0.4635F));

		PartDefinition head_scruff_front = head_main.addOrReplaceChild("head_scruff_front", CubeListBuilder.create().texOffs(17, 26).addBox(-1.5F, -2.0F, 0.0F, 3.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.0F, -3.0F, -0.1745F, 0.0F, 0.0F));

		PartDefinition head_scruff1 = head_main.addOrReplaceChild("head_scruff1", CubeListBuilder.create().texOffs(25, 1).addBox(0.0F, -2.0F, 0.0F, 0.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, -3.0F, -3.0F, -0.3405F, -0.067F, -0.2533F));

		PartDefinition head_scruff3 = head_main.addOrReplaceChild("head_scruff3", CubeListBuilder.create().texOffs(25, 2).addBox(0.0F, -2.0F, 0.0F, 0.0F, 2.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.0F, -3.0F, -0.2618F, 0.0F, 0.0F));

		PartDefinition head_scruff2 = head_main.addOrReplaceChild("head_scruff2", CubeListBuilder.create().texOffs(25, -2).addBox(0.0F, -2.0F, 0.0F, 0.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -3.0F, -3.0F, -0.3405F, 0.067F, 0.2533F));

		PartDefinition leg_front_left = body_base.addOrReplaceChild("leg_front_left", CubeListBuilder.create().texOffs(9, 47).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 6.0F, -9.0F));

		PartDefinition leg_front_right = body_base.addOrReplaceChild("leg_front_right", CubeListBuilder.create().texOffs(9, 47).mirror().addBox(-1.0F, 0.0F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-2.0F, 6.0F, -9.0F));

		PartDefinition leg_back_left = body_base.addOrReplaceChild("leg_back_left", CubeListBuilder.create().texOffs(0, 47).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 6.0F, 0.0F));

		PartDefinition leg_back_right = body_base.addOrReplaceChild("leg_back_right", CubeListBuilder.create().texOffs(0, 47).mirror().addBox(-1.0F, 0.0F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-2.0F, 6.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

    @Override
    public void setupAnim(LivingEntityRenderState state) {
        super.setupAnim(state);

        this.head_main.xRot = state.xRot * (float) (Math.PI / 180.0);
        this.head_main.yRot = state.yRot * (float) (Math.PI / 180.0);
        float animationPos = state.walkAnimationPos;
        float animationSpeed = state.walkAnimationSpeed;

        this.tail.yRot = Mth.cos(animationPos * 0.6662F) * animationSpeed * 0.5f;

        this.leg_back_right.xRot = Mth.cos(animationPos * 0.6662F) * 1.4F * animationSpeed;
        this.leg_back_left.xRot = Mth.cos(animationPos * 0.6662F + (float) Math.PI) * 1.4F * animationSpeed;
        this.leg_front_right.xRot = Mth.cos(animationPos * 0.6662F + (float) Math.PI) * 1.4F * animationSpeed;
        this.leg_front_left.xRot = Mth.cos(animationPos * 0.6662F) * 1.4F * animationSpeed;
    }
}
