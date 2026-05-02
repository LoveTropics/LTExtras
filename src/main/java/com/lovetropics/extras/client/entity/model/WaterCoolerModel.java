package com.lovetropics.extras.client.entity.model;// Made with Blockbench 5.0.4
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.client.entity.animation.WaterCoolerAnimation;
import com.lovetropics.extras.client.entity.state.WaterCoolerRenderState;
import net.minecraft.client.animation.KeyframeAnimation;
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
import net.minecraft.world.entity.Entity;

public class WaterCoolerModel<T extends Entity> extends EntityModel<WaterCoolerRenderState> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(LTExtras.id("water_cooler"), "main");
	private final ModelPart root;
	private final ModelPart water_jug;

    private final KeyframeAnimation shake1Animation;
    private final KeyframeAnimation shake2Animation;
    private final KeyframeAnimation shake3Animation;
    private final KeyframeAnimation shakeDispenseAnimation;

	public WaterCoolerModel(ModelPart root) {
        super(root, RenderTypes::entityTranslucent);
        this.root = root.getChild("root");
		this.water_jug = this.root.getChild("water_jug");
        this.shake1Animation = WaterCoolerAnimation.SHAKE_1.bake(root);
        this.shake2Animation = WaterCoolerAnimation.SHAKE_2.bake(root);
        this.shake3Animation = WaterCoolerAnimation.SHAKE_3.bake(root);
        this.shakeDispenseAnimation = WaterCoolerAnimation.SHAKE_DISPENSE.bake(root);

	}

	public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -18.0F, -7.0F, 10.0F, 18.0F, 9.0F, new CubeDeformation(0.0F))
                .texOffs(32, 37).addBox(-3.0F, -19.0F, -5.0F, 6.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(38, 0).addBox(-5.0F, -18.0F, 2.0F, 10.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(32, 27).addBox(-5.0F, -9.0F, 2.0F, 10.0F, 9.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(38, 4).addBox(-4.0F, -10.0F, 2.0F, 8.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(38, 7).addBox(3.0F, -15.0F, 2.0F, 2.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(38, 14).addBox(-5.0F, -15.0F, 2.0F, 2.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(38, 21).addBox(-2.0F, -14.0F, 1.5F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(38, 24).addBox(1.0F, -14.0F, 1.5F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition water_jug = root.addOrReplaceChild("water_jug", CubeListBuilder.create().texOffs(0, 27).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(0, 45).addBox(-3.0F, -7.0F, -3.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(8, 47).addBox(-2.0F, -1.0F, -2.0F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -19.0F, -2.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    public void setupAnim(WaterCoolerRenderState state) {
        super.setupAnim(state);

        this.shake1Animation.apply(state.shake1AnimationState, state.ageInTicks);
        this.shake2Animation.apply(state.shake2AnimationState, state.ageInTicks);
        this.shake3Animation.apply(state.shake3AnimationState, state.ageInTicks);
        this.shakeDispenseAnimation.apply(state.shakeDispenseAnimationState, state.ageInTicks);
    }
}
