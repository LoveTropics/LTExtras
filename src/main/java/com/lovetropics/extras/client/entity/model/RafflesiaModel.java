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
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;

public class RafflesiaModel extends EntityModel<EntityRenderState> {
	public static final ModelLayerLocation LAYER = new ModelLayerLocation(LTExtras.id("lt_rafflesia"), "main");
	private final ModelPart base;
	private final ModelPart leaf1a;
	private final ModelPart leaf1a2;
	private final ModelPart leaf1a3;
	private final ModelPart leaf1a4;
	private final ModelPart leaf1a5;
	private final ModelPart leaf1a6;
	private final ModelPart leaf1a7;
	private final ModelPart leaf1a8;
	private final ModelPart leaf1a9;
	private final ModelPart leaf1a10;

	public RafflesiaModel(ModelPart root) {
        super(root);
        this.base = root.getChild("base");
		this.leaf1a = this.base.getChild("leaf1a");
		this.leaf1a2 = this.leaf1a.getChild("leaf1a2");
		this.leaf1a3 = this.base.getChild("leaf1a3");
		this.leaf1a4 = this.leaf1a3.getChild("leaf1a4");
		this.leaf1a5 = this.base.getChild("leaf1a5");
		this.leaf1a6 = this.leaf1a5.getChild("leaf1a6");
		this.leaf1a7 = this.base.getChild("leaf1a7");
		this.leaf1a8 = this.leaf1a7.getChild("leaf1a8");
		this.leaf1a9 = this.base.getChild("leaf1a9");
		this.leaf1a10 = this.leaf1a9.getChild("leaf1a10");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition base = partdefinition.addOrReplaceChild("base", CubeListBuilder.create().texOffs(19, 1).addBox(-2.5F, -3.0F, -2.5F, 5.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-3.0F, -1.0F, -3.0F, 6.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(19, 1).addBox(-2.5F, -3.0F, 2.5F, 5.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(14, 8).addBox(-2.5F, -3.0F, -2.5F, 5.0F, 0.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(0, 26).addBox(-1.5F, -2.0F, -1.5F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition cup_r1 = base.addOrReplaceChild("cup_r1", CubeListBuilder.create().texOffs(19, 1).addBox(-2.5F, -2.0F, -2.5F, 5.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(19, 1).addBox(-2.5F, -2.0F, 2.5F, 5.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

		PartDefinition leaf1a = base.addOrReplaceChild("leaf1a", CubeListBuilder.create().texOffs(0, 10).addBox(-3.0F, -1.0F, 0.0F, 6.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 2.5F, 0.0873F, 0.0F, 0.0F));

		PartDefinition leaf1a2 = leaf1a.addOrReplaceChild("leaf1a2", CubeListBuilder.create().texOffs(5, 8).addBox(-2.0F, 0.0F, 0.0F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, 5.0F, -0.2618F, 0.0F, 0.0F));

		PartDefinition leaf1a3 = base.addOrReplaceChild("leaf1a3", CubeListBuilder.create().texOffs(0, 19).addBox(-3.0F, -1.0F, 0.0F, 6.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.3776F, 0.0F, 0.7725F, 0.0873F, 1.2566F, 0.0F));

		PartDefinition leaf1a4 = leaf1a3.addOrReplaceChild("leaf1a4", CubeListBuilder.create().texOffs(5, 17).addBox(-2.0F, 0.0F, 0.0F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, 5.0F, -0.2618F, 0.0F, 0.0F));

		PartDefinition leaf1a5 = base.addOrReplaceChild("leaf1a5", CubeListBuilder.create().texOffs(0, 10).addBox(-3.0F, -1.0F, 0.0F, 6.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.4695F, 0.0F, -2.0225F, -3.0543F, 0.6283F, 3.1416F));

		PartDefinition leaf1a6 = leaf1a5.addOrReplaceChild("leaf1a6", CubeListBuilder.create().texOffs(5, 8).addBox(-2.0F, 0.0F, 0.0F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, 5.0F, -0.2618F, 0.0F, 0.0F));

		PartDefinition leaf1a7 = base.addOrReplaceChild("leaf1a7", CubeListBuilder.create().texOffs(0, 10).addBox(-3.0F, -1.0F, 0.0F, 6.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.3776F, 0.0F, 0.7725F, 0.0873F, -1.2566F, 0.0F));

		PartDefinition leaf1a8 = leaf1a7.addOrReplaceChild("leaf1a8", CubeListBuilder.create().texOffs(5, 8).addBox(-2.0F, 0.0F, 0.0F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, 5.0F, -0.2618F, 0.0F, 0.0F));

		PartDefinition leaf1a9 = base.addOrReplaceChild("leaf1a9", CubeListBuilder.create().texOffs(0, 19).addBox(-3.0F, -1.0F, 0.0F, 6.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.4695F, 0.0F, -2.0225F, -3.0543F, -0.6283F, 3.1416F));

		PartDefinition leaf1a10 = leaf1a9.addOrReplaceChild("leaf1a10", CubeListBuilder.create().texOffs(5, 17).addBox(-2.0F, 0.0F, 0.0F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, 5.0F, -0.2618F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 32, 32);
	}
}
