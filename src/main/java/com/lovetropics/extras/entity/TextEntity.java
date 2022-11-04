package com.lovetropics.extras.entity;

import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.Objects;

public class TextEntity extends Entity {
	private static final EntityDataAccessor<Component> DATA_TEXT = SynchedEntityData.defineId(TextEntity.class, EntityDataSerializers.COMPONENT);
	private static final EntityDataAccessor<Float> DATA_SCALE = SynchedEntityData.defineId(TextEntity.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Float> DATA_FORWARD_X = SynchedEntityData.defineId(TextEntity.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Float> DATA_FORWARD_Y = SynchedEntityData.defineId(TextEntity.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Float> DATA_FORWARD_Z = SynchedEntityData.defineId(TextEntity.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Boolean> DATA_FULLBRIGHT = SynchedEntityData.defineId(TextEntity.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Float> DATA_TARGET_ALPHA = SynchedEntityData.defineId(TextEntity.class, EntityDataSerializers.FLOAT);

	private static final float ALPHA_LERP_SPEED = 0.1f;

	private float alpha = 1.0f;
	private float prevAlpha = 1.0f;

	@Nullable
	private Matrix4f pointAlongMatrix;
	private boolean pointAlongMatrixValid;

	public TextEntity(EntityType<?> type, Level level) {
		super(type, level);
		noCulling = true;
	}

	@Override
	protected void defineSynchedData() {
		entityData.define(DATA_TEXT, TextComponent.EMPTY);
		entityData.define(DATA_SCALE, 0.0625f);
		entityData.define(DATA_FORWARD_X, 0.0f);
		entityData.define(DATA_FORWARD_Y, 0.0f);
		entityData.define(DATA_FORWARD_Z, 0.0f);
		entityData.define(DATA_FULLBRIGHT, false);
		entityData.define(DATA_TARGET_ALPHA, 1.0f);
	}

	@Override
	public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
		super.onSyncedDataUpdated(key);
		if (DATA_FORWARD_X.equals(key) || DATA_FORWARD_Y.equals(key) || DATA_FORWARD_Z.equals(key)) {
			pointAlongMatrixValid = false;
		} else if (DATA_TARGET_ALPHA.equals(key) && firstTick) {
			prevAlpha = alpha = entityData.get(DATA_TARGET_ALPHA);
		}
	}

	@Override
	public void tick() {
		super.tick();
		prevAlpha = alpha;
		float delta = targetAlpha() - alpha;
		if (delta > 0.01f) {
			alpha += Math.min(ALPHA_LERP_SPEED, delta);
		} else if (delta < -0.01f) {
			alpha += Math.max(-ALPHA_LERP_SPEED, delta);
		}
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag tag) {
		if (tag.contains("text", Tag.TAG_STRING)) {
			try {
				setText(Component.Serializer.fromJson(tag.getString("text")));
			} catch (Exception ignored) {
			}
		}

		if (tag.contains("scale", Tag.TAG_FLOAT)) {
			setScale(tag.getFloat("scale"));
		}

		if (tag.contains("forward", Tag.TAG_COMPOUND)) {
			CompoundTag forward = tag.getCompound("forward");
			setForward(new Vector3f(forward.getFloat("x"), forward.getFloat("y"), forward.getFloat("z")));
		}

		if (tag.contains("fullbright", Tag.TAG_BYTE)) {
			setFullbright(tag.getBoolean("fullbright"));
		}

		if (tag.contains("alpha", Tag.TAG_FLOAT)) {
			setTargetAlpha(tag.getFloat("alpha"));
		}
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag tag) {
		tag.putString("text", Component.Serializer.toJson(text()));
		tag.putFloat("scale", scale());

		Vector3f forward = forward();
		if (forward != null) {
			CompoundTag forwardTag = new CompoundTag();
			forwardTag.putFloat("x", forward.x());
			forwardTag.putFloat("y", forward.y());
			forwardTag.putFloat("z", forward.z());
		}

		tag.putBoolean("fullbright", fullbright());

		tag.putFloat("alpha", targetAlpha());
	}

	@Override
	public Packet<?> getAddEntityPacket() {
		return new ClientboundAddEntityPacket(this);
	}

	public void setText(Component text) {
		entityData.set(DATA_TEXT, text);
	}

	public Component text() {
		return entityData.get(DATA_TEXT);
	}

	public void setScale(float scale) {
		entityData.set(DATA_SCALE, scale);
	}

	public float scale() {
		return entityData.get(DATA_SCALE);
	}

	public void setForward(@Nullable Vector3f forward) {
		forward = Objects.requireNonNullElse(forward, Vector3f.ZERO);
		entityData.set(DATA_FORWARD_X, forward.x());
		entityData.set(DATA_FORWARD_Y, forward.y());
		entityData.set(DATA_FORWARD_Z, forward.z());
	}

	@Nullable
	public Vector3f forward() {
		float x = entityData.get(DATA_FORWARD_X);
		float y = entityData.get(DATA_FORWARD_Y);
		float z = entityData.get(DATA_FORWARD_Z);
		if (Mth.equal(x, 0.0f) && Mth.equal(y, 0.0f) && Mth.equal(z, 0.0f)) {
			return null;
		}
		return new Vector3f(x, y, z);
	}

	public void setFullbright(boolean fullbright) {
		entityData.set(DATA_FULLBRIGHT, fullbright);
	}

	public boolean fullbright() {
		return entityData.get(DATA_FULLBRIGHT);
	}

	public void setTargetAlpha(float alpha) {
		entityData.set(DATA_TARGET_ALPHA, alpha);
	}

	public float targetAlpha() {
		return entityData.get(DATA_TARGET_ALPHA);
	}

	public float alpha(float partialTicks) {
		return Mth.lerp(partialTicks, prevAlpha, alpha);
	}

	@Nullable
	public Matrix4f pointAlongMatrix() {
		if (!pointAlongMatrixValid) {
			Vector3f forward = forward();
			pointAlongMatrix = forward != null ? createPointAlongMatrix(forward) : null;
			pointAlongMatrixValid = true;
		}
		return pointAlongMatrix;
	}

	private static Matrix4f createPointAlongMatrix(Vector3f pointAlong) {
		pointAlong = pointAlong.copy();
		pointAlong.mul(-1.0F);
		pointAlong.normalize();

		Vector3f left = Vector3f.YP.copy();
		left.cross(pointAlong);
		left.normalize();

		Vector3f up = pointAlong.copy();
		up.cross(left);
		up.normalize();

		return new Matrix4f(new float[] {
				left.x(), up.x(), pointAlong.x(), 0.0F,
				left.y(), up.y(), pointAlong.y(), 0.0F,
				left.z(), up.z(), pointAlong.z(), 0.0F,
				0.0F, 0.0F, 0.0F, 1.0F
		});
	}
}
