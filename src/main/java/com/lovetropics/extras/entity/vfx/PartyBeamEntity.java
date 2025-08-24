package com.lovetropics.extras.entity.vfx;

import com.lovetropics.extras.LTExtras;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.ARGB;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.ExplosionEvent;
import org.joml.Vector3f;

import javax.annotation.Nullable;

@EventBusSubscriber(modid = LTExtras.MODID)
public class PartyBeamEntity extends EndCrystal {
    private static final EntityDataAccessor<Vector3f> DATA_COLOR = SynchedEntityData.defineId(PartyBeamEntity.class, EntityDataSerializers.VECTOR3);

    @Nullable
    private BlockPos targetPos = null;

    public PartyBeamEntity(EntityType<? extends EndCrystal> type, Level level) {
        super(type, level);
    }

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide) {
            if (targetPos != null && level().getGameTime() % 100 == 0) {
                random.setSeed(level().getGameTime());

                int ax = targetPos.getX() + (random.nextInt(5) - random.nextInt(5));
                int az = targetPos.getZ() + (random.nextInt(5) - random.nextInt(5));

                int packed = Mth.hsvToRgb(random.nextFloat(), 0.8f, 0.8F);
				int r = ARGB.red(packed);
				int g = ARGB.green(packed);
				int b = ARGB.blue(packed);

                setColor(new Vector3f(r / 255.0f, g / 255.0f, b / 255.0f));

                setBeamTarget(new BlockPos(ax, targetPos.getY(), az));
            }
        }
    }

	@Override
	protected void addAdditionalSaveData(ValueOutput output) {
		super.addAdditionalSaveData(output);
		output.storeNullable("TargetPos", BlockPos.CODEC, targetPos);
		output.store("TargetColor", ExtraCodecs.VECTOR3F, getColor());
    }

	@Override
	protected void readAdditionalSaveData(ValueInput input) {
		super.readAdditionalSaveData(input);
		targetPos = input.read("TargetPos", BlockPos.CODEC).orElse(null);
		setColor(input.read("TargetColor", ExtraCodecs.VECTOR3F).orElse(new Vector3f()));
	}

    public void setColor(Vector3f color) {
        getEntityData().set(DATA_COLOR, color);
    }

    public Vector3f getColor() {
        return getEntityData().get(DATA_COLOR);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);

        builder.define(DATA_COLOR, new Vector3f(0.0f, 0.0f, 0.0f));
    }

	@SubscribeEvent
	public static void onExplosionStart(ExplosionEvent.Start event) {
		if (event.getExplosion().getDirectSourceEntity() instanceof PartyBeamEntity) {
			event.setCanceled(true);
		}
	}
}
