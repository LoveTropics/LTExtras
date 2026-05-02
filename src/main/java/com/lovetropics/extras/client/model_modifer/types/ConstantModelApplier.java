package com.lovetropics.extras.client.model_modifer.types;

import com.lovetropics.extras.client.model_modifer.ModelApplier;
import com.lovetropics.extras.model_modifer.types.ConstantType;
import com.lovetropics.extras.model_modifer.types.data.ModelPartData;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

import java.util.Map;

public record ConstantModelApplier() implements ModelApplier<ConstantType.Data> {

    @Override
    public void applyToModel(ConstantType.Data data, LivingEntityRenderState state, EntityModel<?> model) {
        ModelPart root = model.root();
        for (Map.Entry<String, ModelPartData> stringModelPartDataEntry : data.modifiers().entrySet()) {
            String partName = stringModelPartDataEntry.getKey();
            ModelPartData partData = stringModelPartDataEntry.getValue();
            if (root.hasChild(partName)) {
                ModelPart child = root.getChild(partName);
                partData.x().ifPresent(x -> child.x = x);
                partData.y().ifPresent(y -> child.y = y);
                partData.z().ifPresent(z -> child.z = z);
                partData.xRot().ifPresent(xRot -> child.xRot = xRot);
                partData.yRot().ifPresent(yRot -> child.yRot = yRot);
                partData.zRot().ifPresent(zRot -> child.zRot = zRot);
                partData.xScale().ifPresent(xScale -> child.xScale = xScale);
                partData.yScale().ifPresent(yScale -> child.yScale = yScale);
                partData.zScale().ifPresent(zScale -> child.zScale = zScale);
            }
        }
    }
}
