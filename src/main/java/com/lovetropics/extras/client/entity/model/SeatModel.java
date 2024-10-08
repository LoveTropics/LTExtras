// Made with Blockbench 4.8.3
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports
package com.lovetropics.extras.client.entity.model;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.block.entity.SeatEntity;
import com.lovetropics.extras.client.entity.animation.RaveKoaAnimation;
import com.lovetropics.extras.entity.ravekoa.RaveKoaEntity;
import com.lovetropics.extras.entity.ravekoa.RaveKoaEntityDJ;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class SeatModel<T extends SeatEntity> extends HierarchicalModel<T> {

    public SeatModel() {
        //This does nothing because the entity should never be rendered.
    }

    @Override
    public ModelPart root() {
        return null;
    }

    @Override
    public void setupAnim(final T t, final float v, final float v1, final float v2, final float v3, final float v4) {

    }
}
