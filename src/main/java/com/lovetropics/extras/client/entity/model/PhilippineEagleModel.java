package com.lovetropics.extras.client.entity.model;

import com.lovetropics.extras.LTExtras;
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
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.util.Mth;

public class PhilippineEagleModel extends EntityModel<LivingEntityRenderState> {
	public static final ModelLayerLocation LAYER = new ModelLayerLocation(LTExtras.id("lt_philippine_eagle"), "main");
	private final ModelPart body_base;
	private final ModelPart leg_left1a;
	private final ModelPart leg_left1b;
	private final ModelPart talon_left1a;
	private final ModelPart talon_left1b;
	private final ModelPart leg_right1a;
	private final ModelPart leg_right1b;
	private final ModelPart talon_right1a;
	private final ModelPart talon_right1b;
	private final ModelPart neck;
	private final ModelPart head_base;
	private final ModelPart crown_left;
	private final ModelPart crown_right;
	private final ModelPart crown_mid;
	private final ModelPart beak_upper;
	private final ModelPart beak_lower;
	private final ModelPart bird_buttocks;
	private final ModelPart tail_top;
	private final ModelPart tail_bottom;
	private final ModelPart tail_mid_left;
	private final ModelPart tail_mid_right;
	private final ModelPart wing_left1a;
	private final ModelPart wing_left1b;
	private final ModelPart wing_left1c;
	private final ModelPart wing_left_tip;
	private final ModelPart wing_right1a;
	private final ModelPart wing_right1b;
	private final ModelPart wing_right1c;
	private final ModelPart wing_right_tip;

	public PhilippineEagleModel(ModelPart root) {
        super(root, RenderTypes::entityCutoutCull);
		this.body_base = root.getChild("body_base");
		this.leg_left1a = this.body_base.getChild("leg_left1a");
		this.leg_left1b = this.leg_left1a.getChild("leg_left1b");
		this.talon_left1a = this.leg_left1b.getChild("talon_left1a");
		this.talon_left1b = this.talon_left1a.getChild("talon_left1b");
		this.leg_right1a = this.body_base.getChild("leg_right1a");
		this.leg_right1b = this.leg_right1a.getChild("leg_right1b");
		this.talon_right1a = this.leg_right1b.getChild("talon_right1a");
		this.talon_right1b = this.talon_right1a.getChild("talon_right1b");
		this.neck = this.body_base.getChild("neck");
		this.head_base = this.neck.getChild("head_base");
		this.crown_left = this.head_base.getChild("crown_left");
		this.crown_right = this.head_base.getChild("crown_right");
		this.crown_mid = this.head_base.getChild("crown_mid");
		this.beak_upper = this.head_base.getChild("beak_upper");
		this.beak_lower = this.head_base.getChild("beak_lower");
		this.bird_buttocks = this.body_base.getChild("bird_buttocks");
		this.tail_top = this.bird_buttocks.getChild("tail_top");
		this.tail_bottom = this.bird_buttocks.getChild("tail_bottom");
		this.tail_mid_left = this.bird_buttocks.getChild("tail_mid_left");
		this.tail_mid_right = this.bird_buttocks.getChild("tail_mid_right");
		this.wing_left1a = this.body_base.getChild("wing_left1a");
		this.wing_left1b = this.wing_left1a.getChild("wing_left1b");
		this.wing_left1c = this.wing_left1b.getChild("wing_left1c");
		this.wing_left_tip = this.wing_left1c.getChild("wing_left_tip");
		this.wing_right1a = this.body_base.getChild("wing_right1a");
		this.wing_right1b = this.wing_right1a.getChild("wing_right1b");
		this.wing_right1c = this.wing_right1b.getChild("wing_right1c");
		this.wing_right_tip = this.wing_right1c.getChild("wing_right_tip");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body_base = partdefinition.addOrReplaceChild("body_base", CubeListBuilder.create().texOffs(0, 13).addBox(-2.5F, -7.0F, -1.0F, 5.0F, 9.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 18.1F, 0.0F, 0.6109F, 0.0F, 0.0F));

		PartDefinition leg_left1a = body_base.addOrReplaceChild("leg_left1a", CubeListBuilder.create().texOffs(21, 15).addBox(-1.0F, -1.0F, -2.0F, 2.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.5F, 0.0F, 0.25F, -0.5214F, -0.0973F, -0.007F));

		PartDefinition leg_left1b = leg_left1a.addOrReplaceChild("leg_left1b", CubeListBuilder.create().texOffs(23, 24).addBox(-0.5F, 0.0F, -1.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 4.0F, -0.5F, -0.3927F, 0.0F, 0.0F));

		PartDefinition talon_left1a = leg_left1b.addOrReplaceChild("talon_left1a", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 2.0F, 0.0F, 0.3326F, -0.1825F, 0.0141F));

		PartDefinition talon_left1a_r1 = talon_left1a.addOrReplaceChild("talon_left1a_r1", CubeListBuilder.create().texOffs(20, 27).addBox(0.0F, 0.0F, -3.0F, 3.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.7854F, 0.0F));

		PartDefinition talon_left1b = talon_left1a.addOrReplaceChild("talon_left1b", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.1047F, 0.0F, 0.0F));

		PartDefinition talon_left1b_r1 = talon_left1b.addOrReplaceChild("talon_left1b_r1", CubeListBuilder.create().texOffs(22, 30).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.7854F, 0.0F));

		PartDefinition leg_right1a = body_base.addOrReplaceChild("leg_right1a", CubeListBuilder.create().texOffs(21, 15).mirror().addBox(-1.0F, -1.0F, -2.0F, 2.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-1.5F, 0.0F, 0.25F, -0.5214F, 0.0973F, 0.007F));

		PartDefinition leg_right1b = leg_right1a.addOrReplaceChild("leg_right1b", CubeListBuilder.create().texOffs(23, 24).mirror().addBox(-0.5F, 0.0F, -1.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 4.0F, -0.5F, -0.3927F, 0.0F, 0.0F));

		PartDefinition talon_right1a = leg_right1b.addOrReplaceChild("talon_right1a", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 2.0F, 0.0F, 0.3326F, 0.1825F, -0.0141F));

		PartDefinition talon_right1a_r1 = talon_right1a.addOrReplaceChild("talon_right1a_r1", CubeListBuilder.create().texOffs(20, 27).mirror().addBox(-3.0F, 0.0F, -3.0F, 3.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

		PartDefinition talon_right1b = talon_right1a.addOrReplaceChild("talon_right1b", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.1047F, 0.0F, 0.0F));

		PartDefinition talon_right1b_r1 = talon_right1b.addOrReplaceChild("talon_right1b_r1", CubeListBuilder.create().texOffs(22, 30).mirror().addBox(0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

		PartDefinition neck = body_base.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(0, 7).addBox(-1.5F, -3.0F, 0.0F, 3.0F, 3.0F, 3.0F, new CubeDeformation(-0.01F)), PartPose.offsetAndRotation(0.0F, -7.0F, -0.5F, -0.6981F, 0.0F, 0.0F));

		PartDefinition head_base = neck.addOrReplaceChild("head_base", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -3.0F, -4.0F, 3.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.0F, 3.0F, 0.3927F, 0.0F, 0.0F));

		PartDefinition crown_left = head_base.addOrReplaceChild("crown_left", CubeListBuilder.create().texOffs(15, 7).addBox(0.0F, -1.5F, 0.0F, 0.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.5F, -1.5F, -2.0F, 0.0F, 0.2618F, 0.0F));

		PartDefinition crown_right = head_base.addOrReplaceChild("crown_right", CubeListBuilder.create().texOffs(15, 7).mirror().addBox(0.0F, -1.5F, 0.0F, 0.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-1.5F, -1.5F, -2.0F, 0.0F, -0.2618F, 0.0F));

		PartDefinition crown_mid = head_base.addOrReplaceChild("crown_mid", CubeListBuilder.create().texOffs(12, 7).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.0F, -2.0F, 0.2618F, 0.0F, 0.0F));

		PartDefinition beak_upper = head_base.addOrReplaceChild("beak_upper", CubeListBuilder.create().texOffs(15, 0).addBox(-0.5F, 0.0F, -3.0F, 1.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(18, 5).addBox(-0.5F, 2.0F, -3.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.0F, -4.0F, 0.0873F, 0.0F, 0.0F));

		PartDefinition beak_lower = head_base.addOrReplaceChild("beak_lower", CubeListBuilder.create().texOffs(24, 1).addBox(-0.5F, -0.5F, -3.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(-0.01F)), PartPose.offsetAndRotation(0.0F, -0.5F, -3.7F, -0.0436F, 0.0F, 0.0F));

		PartDefinition bird_buttocks = body_base.addOrReplaceChild("bird_buttocks", CubeListBuilder.create().texOffs(4, 28).addBox(-2.0F, 0.0F, 0.0F, 4.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.9F, 4.0F, -2.138F, 0.0F, 0.0F));

		PartDefinition tail_top = bird_buttocks.addOrReplaceChild("tail_top", CubeListBuilder.create().texOffs(-8, 33).addBox(-2.0F, 0.0F, 0.0F, 4.0F, 0.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.5672F, 0.0F, 0.0F));

		PartDefinition tail_bottom = bird_buttocks.addOrReplaceChild("tail_bottom", CubeListBuilder.create().texOffs(-6, 47).addBox(-2.0F, 0.0F, 0.0F, 4.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.5F, 2.0F, 0.9163F, 0.0F, 0.0F));

		PartDefinition tail_mid_left = bird_buttocks.addOrReplaceChild("tail_mid_left", CubeListBuilder.create().texOffs(-6, 41).mirror().addBox(-2.5F, 0.0F, 0.0F, 4.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.5F, 0.0F, 2.0F, 0.7418F, 0.0F, 0.0F));

		PartDefinition tail_mid_right = bird_buttocks.addOrReplaceChild("tail_mid_right", CubeListBuilder.create().texOffs(-6, 41).addBox(-1.5F, 0.0F, 0.0F, 4.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, 0.05F, 2.0F, 0.7418F, 0.0F, 0.0F));

		PartDefinition wing_left1a = body_base.addOrReplaceChild("wing_left1a", CubeListBuilder.create().texOffs(0, 57).addBox(0.0F, -1.5F, -1.0F, 1.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.25F, -5.25F, 2.3F, -1.5238F, 0.0736F, 0.569F));

		PartDefinition wing_left1b = wing_left1a.addOrReplaceChild("wing_left1b", CubeListBuilder.create().texOffs(9, 56).addBox(-1.0F, -0.9F, 0.0F, 1.0F, 4.0F, 4.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(1.0F, -0.6F, 2.0F, 0.0F, -2.4871F, 0.0F));

		PartDefinition wing_left1c = wing_left1b.addOrReplaceChild("wing_left1c", CubeListBuilder.create().texOffs(20, 52).addBox(0.0F, -0.9F, 0.0F, 1.0F, 4.0F, 8.0F, new CubeDeformation(0.02F)), PartPose.offsetAndRotation(-1.0F, 0.0F, 4.0F, 0.0F, 3.0107F, 0.0F));

		PartDefinition wing_left_tip = wing_left1c.addOrReplaceChild("wing_left_tip", CubeListBuilder.create().texOffs(39, 59).addBox(0.0F, -0.9F, 0.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(0.02F)), PartPose.offset(0.0F, 1.0F, 8.0F));

		PartDefinition wing_right1a = body_base.addOrReplaceChild("wing_right1a", CubeListBuilder.create().texOffs(0, 57).mirror().addBox(-1.0F, -1.5F, -1.0F, 1.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-2.25F, -5.25F, 2.3F, -1.5238F, -0.0736F, -0.569F));

		PartDefinition wing_right1b = wing_right1a.addOrReplaceChild("wing_right1b", CubeListBuilder.create().texOffs(9, 56).mirror().addBox(0.0F, -0.9F, 0.0F, 1.0F, 4.0F, 4.0F, new CubeDeformation(0.01F)).mirror(false), PartPose.offsetAndRotation(-1.0F, -0.6F, 2.0F, 0.0F, 2.4871F, 0.0F));

		PartDefinition wing_right1c = wing_right1b.addOrReplaceChild("wing_right1c", CubeListBuilder.create().texOffs(20, 52).mirror().addBox(-1.0F, -0.9F, 0.0F, 1.0F, 4.0F, 8.0F, new CubeDeformation(0.02F)).mirror(false), PartPose.offsetAndRotation(1.0F, 0.0F, 4.0F, 0.0F, -3.0107F, 0.0F));

		PartDefinition wing_right_tip = wing_right1c.addOrReplaceChild("wing_right_tip", CubeListBuilder.create().texOffs(39, 59).mirror().addBox(-1.0F, -0.9F, 0.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(0.02F)).mirror(false), PartPose.offset(0.0F, 1.0F, 8.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

    @Override
    public void setupAnim(LivingEntityRenderState state) {
        super.setupAnim(state);

        this.head_base.xRot = state.xRot * (float) (Math.PI / 180.0);
        this.head_base.yRot = state.yRot * (float) (Math.PI / 180.0);

        float animationSpeed = state.walkAnimationSpeed;
        float animationPos = state.walkAnimationPos;
        this.leg_right1a.xRot = Mth.cos(animationPos * 0.6662F) * 1.4F * animationSpeed;
        this.leg_left1a.xRot = Mth.cos(animationPos * 0.6662F + (float) Math.PI) * 1.4F * animationSpeed;
    }
}
