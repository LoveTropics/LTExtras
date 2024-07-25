// Made with Blockbench 4.8.3
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports
package com.lovetropics.extras.client.entity.model;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.client.entity.animation.RaveKoaAnimation;
import com.lovetropics.extras.entity.ravekoa.RaveKoaEntity;
import com.lovetropics.extras.entity.ravekoa.RaveKoaEntityDJ;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class RaveKoaModel<T extends RaveKoaEntity> extends HierarchicalModel<T> {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(LTExtras.location("rave_koa_model"), "main");
    private final ModelPart CenterPivot;
    private final ModelPart booth;
    private final ModelPart root;

    public RaveKoaModel(ModelPart root) {
        this.root = root;
        CenterPivot = root.getChild("CenterPivot");
        booth = root.getChild("booth");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition CenterPivot = partdefinition.addOrReplaceChild("CenterPivot", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 30.0F, 0.0F, 0.0F, 0.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 8.0F, 0.0F));

        PartDefinition Body = CenterPivot.addOrReplaceChild("Body", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, -12.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.0F, 0.0F));

        PartDefinition Head = Body.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 2).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, 0.0F));

        PartDefinition hat2 = Head.addOrReplaceChild("hat2", CubeListBuilder.create().texOffs(24, 1).addBox(-7.0F, -10.0F, -3.0F, 10.0F, 1.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 3.0F, -2.0F));

        PartDefinition headphones = Head.addOrReplaceChild("headphones", CubeListBuilder.create().texOffs(36, 2).addBox(3.0F, -6.0F, -2.0F, 2.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(34, 2).mirror().addBox(-5.0F, -6.0F, -2.0F, 2.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(33, 5).addBox(-4.75F, -8.5F, -1.0F, 9.5F, 5.5F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition LArm = Body.addOrReplaceChild("LArm", CubeListBuilder.create().texOffs(40, 16).addBox(-0.5F, -2.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(4.5F, -10.0F, 0.0F));

        PartDefinition LArmLower = LArm.addOrReplaceChild("LArmLower", CubeListBuilder.create().texOffs(40, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(-0.01F)), PartPose.offset(1.5F, 3.5F, 0.0F));

        PartDefinition RArm = Body.addOrReplaceChild("RArm", CubeListBuilder.create().texOffs(40, 16).addBox(-3.5F, -2.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.5F, -10.0F, 0.0F));

        PartDefinition RArmLower = RArm.addOrReplaceChild("RArmLower", CubeListBuilder.create().texOffs(40, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(-0.01F)), PartPose.offset(-1.5F, 3.5F, 0.0F));

        PartDefinition LLeg = CenterPivot.addOrReplaceChild("LLeg", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 4.0F, 0.0F));

        PartDefinition LLeg2 = LLeg.addOrReplaceChild("LLeg2", CubeListBuilder.create().texOffs(47, 34).addBox(-2.05F, 0.0F, 0.0F, 4.05F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.0F, -2.0F));

        PartDefinition RLeg = CenterPivot.addOrReplaceChild("RLeg", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 4.0F, 0.0F));

        PartDefinition RLeg2 = RLeg.addOrReplaceChild("RLeg2", CubeListBuilder.create().texOffs(47, 34).addBox(-2.05F, 0.0F, 0.0F, 4.05F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.0F, -2.0F));

        PartDefinition booth = partdefinition.addOrReplaceChild("booth", CubeListBuilder.create().texOffs(6, 34).addBox(-6.0F, -16.0F, -16.0F, 12.0F, 16.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        root().getAllParts().forEach(ModelPart::resetPose);
        if (!(entity instanceof RaveKoaEntityDJ)) {
            booth.skipDraw = true;
        }
        animate(entity.raveAnimationStateDJ, RaveKoaAnimation.PLAYER_ELBOWS_DJ_KEYFRAMED, ageInTicks);
        animate(entity.raveAnimationStateDance1, RaveKoaAnimation.PLAYER_ELBOWS_DANCE1_KEYFRAMED, ageInTicks);
        animate(entity.raveAnimationStateDance2, RaveKoaAnimation.PLAYER_ELBOWS_DANCE3_KEYFRAMED, ageInTicks);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        CenterPivot.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        booth.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }

    @Override
    public ModelPart root() {
        return root;
    }
}
