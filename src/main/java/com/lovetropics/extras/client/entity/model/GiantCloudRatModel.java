package com.lovetropics.extras.client.entity.model;// Made with Blockbench 5.0.7
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports

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

public class GiantCloudRatModel extends EntityModel<LivingEntityRenderState>  {
	public static final ModelLayerLocation LAYER = new ModelLayerLocation(LTExtras.id("lt_n_luzon_giant_cloud_rat"), "main");
	private final ModelPart body_base;
	private final ModelPart head_main;
	private final ModelPart cute_boopable_nose;
	private final ModelPart ear_left;
	private final ModelPart ear_right;
	private final ModelPart whiskers_right1;
	private final ModelPart whiskers_left1;
	private final ModelPart leg_front_left;
	private final ModelPart leg_front_right;
	private final ModelPart leg_back_left;
	private final ModelPart leg_back_right;
	private final ModelPart tail1a;
	private final ModelPart tail1b;
	private final ModelPart tail1c;

	public GiantCloudRatModel(ModelPart root) {
        super(root);
		this.body_base = root.getChild("body_base");
		this.head_main = this.body_base.getChild("head_main");
		this.cute_boopable_nose = this.head_main.getChild("cute_boopable_nose");
		this.ear_left = this.head_main.getChild("ear_left");
		this.ear_right = this.head_main.getChild("ear_right");
		this.whiskers_right1 = this.head_main.getChild("whiskers_right1");
		this.whiskers_left1 = this.head_main.getChild("whiskers_left1");
		this.leg_front_left = this.body_base.getChild("leg_front_left");
		this.leg_front_right = this.body_base.getChild("leg_front_right");
		this.leg_back_left = this.body_base.getChild("leg_back_left");
		this.leg_back_right = this.body_base.getChild("leg_back_right");
		this.tail1a = this.body_base.getChild("tail1a");
		this.tail1b = this.tail1a.getChild("tail1b");
		this.tail1c = this.tail1b.getChild("tail1c");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body_base = partdefinition.addOrReplaceChild("body_base", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -3.0F, -4.0F, 3.0F, 3.0F, 5.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, 23.0F, 2.0F, -0.0436F, 0.0F, 0.0F));

		PartDefinition head_main = body_base.addOrReplaceChild("head_main", CubeListBuilder.create().texOffs(0, 8).addBox(-1.0F, -0.5F, -3.0F, 2.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.0F, -4.0F, 0.3927F, 0.0F, 0.0F));

		PartDefinition cute_boopable_nose = head_main.addOrReplaceChild("cute_boopable_nose", CubeListBuilder.create(), PartPose.offset(0.0F, -0.5F, -3.0F));

		PartDefinition cute_boopable_nose_r1 = cute_boopable_nose.addOrReplaceChild("cute_boopable_nose_r1", CubeListBuilder.create().texOffs(0, 3).addBox(-0.5F, 0.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.7854F, 0.0F, 0.0F));

		PartDefinition ear_left = head_main.addOrReplaceChild("ear_left", CubeListBuilder.create().texOffs(0, 8).addBox(0.0F, -1.0F, 0.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -0.5F, -1.0F, -0.7459F, 0.274F, 0.2849F));

		PartDefinition ear_right = head_main.addOrReplaceChild("ear_right", CubeListBuilder.create().texOffs(0, 8).addBox(0.0F, -1.0F, 0.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, -0.5F, -1.0F, -0.7459F, -0.274F, -0.2849F));

		PartDefinition whiskers_right1 = head_main.addOrReplaceChild("whiskers_right1", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.0F, 0.5F, -3.0F, 0.0F, 0.0F, -0.3927F));

		PartDefinition whiskers_right1_r1 = whiskers_right1.addOrReplaceChild("whiskers_right1_r1", CubeListBuilder.create().texOffs(12, 0).mirror().addBox(-1.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

		PartDefinition whiskers_left1 = head_main.addOrReplaceChild("whiskers_left1", CubeListBuilder.create(), PartPose.offsetAndRotation(1.0F, 0.5F, -3.0F, 0.0F, 0.0F, 0.3927F));

		PartDefinition whiskers_left1_r1 = whiskers_left1.addOrReplaceChild("whiskers_left1_r1", CubeListBuilder.create().texOffs(12, 0).addBox(0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.7854F, 0.0F));

		PartDefinition leg_front_left = body_base.addOrReplaceChild("leg_front_left", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, -0.9F, -3.5F, -0.1298F, -0.017F, -0.1298F));

		PartDefinition leg_front_right = body_base.addOrReplaceChild("leg_front_right", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-1.0F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-0.5F, -0.9F, -3.5F, -0.1298F, 0.017F, 0.1298F));

		PartDefinition leg_back_left = body_base.addOrReplaceChild("leg_back_left", CubeListBuilder.create().texOffs(10, 9).addBox(-1.0F, 0.0F, -2.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.5F, -0.5F, 0.5F, 0.2756F, -0.1513F, -0.0853F));

		PartDefinition leg_back_right = body_base.addOrReplaceChild("leg_back_right", CubeListBuilder.create().texOffs(10, 9).mirror().addBox(0.0F, 0.0F, -2.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-1.5F, -0.5F, 0.5F, 0.2756F, 0.1513F, 0.0853F));

		PartDefinition tail1a = body_base.addOrReplaceChild("tail1a", CubeListBuilder.create().texOffs(0, 13).addBox(-0.5F, 0.0F, 0.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.0F, 1.0F, -0.829F, 0.0F, 0.0F));

		PartDefinition tail1b = tail1a.addOrReplaceChild("tail1b", CubeListBuilder.create().texOffs(6, 13).addBox(-0.5F, -1.0F, 0.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, 1.0F, 2.0F, 0.48F, 0.0F, 0.0F));

		PartDefinition tail1c = tail1b.addOrReplaceChild("tail1c", CubeListBuilder.create().texOffs(10, 12).addBox(-0.5F, -1.0F, 0.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 2.0F, 0.3927F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 16, 16);
	}

    @Override
    public void setupAnim(LivingEntityRenderState state) {
        super.setupAnim(state);

        this.head_main.xRot = state.xRot * (float) (Math.PI / 180.0);
        this.head_main.yRot = state.yRot * (float) (Math.PI / 180.0);
        float animationPos = state.walkAnimationPos;
        float animationSpeed = state.walkAnimationSpeed;

        this.tail1a.yRot = Mth.cos(animationPos * 0.6662F) * animationSpeed * 0.5f;

        this.leg_back_right.xRot = Mth.cos(animationPos * 0.6662F) * 1.4F * animationSpeed;
        this.leg_back_left.xRot = Mth.cos(animationPos * 0.6662F + (float) Math.PI) * 1.4F * animationSpeed;
        this.leg_front_right.xRot = Mth.cos(animationPos * 0.6662F + (float) Math.PI) * 1.4F * animationSpeed;
        this.leg_front_left.xRot = Mth.cos(animationPos * 0.6662F) * 1.4F * animationSpeed;
    }
}
