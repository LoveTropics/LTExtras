package com.lovetropics.extras.client.model_modifer.types;

import com.lovetropics.extras.client.model_modifer.ModelApplier;
import com.lovetropics.extras.model_modifer.types.OperationType;
import com.lovetropics.extras.model_modifer.types.data.ModelPartData;
import com.lovetropics.extras.model_modifer.types.data.Operation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

import java.util.Map;

public record OperationModelApplier() implements ModelApplier<OperationType.Modifier> {

    @Override
    public void applyToModel(OperationType.Modifier data, LivingEntityRenderState state, EntityModel<?> model) {
        ModelPart root = model.root();
        Operation operation = data.operation();
        for (Map.Entry<String, ModelPartData> stringModelPartDataEntry : data.modifiers().entrySet()) {
            String partName = stringModelPartDataEntry.getKey();
            ModelPartData partData = stringModelPartDataEntry.getValue();
            if (root.hasChild(partName)) {
                ModelPart child = root.getChild(partName);
                partData.x().ifPresent(x -> child.x = operation.apply(child.x, x));
                partData.y().ifPresent(y -> child.y = operation.apply(child.y, y));
                partData.z().ifPresent(z -> child.z = operation.apply(child.z, z));
                partData.xRot().ifPresent(xRot -> child.xRot = operation.apply(child.xRot, xRot));
                partData.yRot().ifPresent(yRot -> child.yRot = operation.apply(child.yRot, yRot));
                partData.zRot().ifPresent(zRot -> child.zRot = operation.apply(child.zRot, zRot));
                partData.xScale().ifPresent(xScale -> child.xScale = operation.apply(child.xScale, xScale));
                partData.yScale().ifPresent(yScale -> child.yScale = operation.apply(child.yScale, yScale));
                partData.zScale().ifPresent(zScale -> child.zScale = operation.apply(child.zScale, zScale));
            }
        }
    }
}
