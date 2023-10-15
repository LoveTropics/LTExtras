package com.lovetropics.extras;

import org.joml.Quaternionf;
import org.joml.Vector3f;

public class ExtraUtils {
    private static final Vector3f Y_AXIS = new Vector3f(0.0f, 1.0f, 0.0f);

    public static Quaternionf rotationAboutY(final Quaternionf rotation, final Quaternionf result) {
        return rotationAbout(rotation, Y_AXIS, result);
    }

    public static Quaternionf rotationAbout(final Quaternionf rotation, final Vector3f axis, final Quaternionf result) {
        final float projectScale = axis.dot(rotation.x(), rotation.y(), rotation.z());
        return result.set(axis.x() * projectScale, axis.y() * projectScale, axis.z() * projectScale, rotation.w());
    }
}
