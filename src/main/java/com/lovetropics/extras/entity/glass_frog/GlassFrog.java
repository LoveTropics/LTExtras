package com.lovetropics.extras.entity.glass_frog;

import com.google.common.collect.ImmutableList;
import com.lovetropics.extras.entity.ExtraEntities;
import com.mojang.serialization.Dynamic;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.camel.Camel;
import net.minecraft.world.entity.animal.camel.CamelAi;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.animal.frog.FrogAi;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.AmphibiousNodeEvaluator;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.PathfindingContext;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

/**
 * A small, translucent frog native to Amazonian streams. Serves as a bioindicator for healthy freshwater ecosystems and provides food for larger predators.
 * <p>
 * Mostly code borrowed from the vanilla frog.
 */
public class GlassFrog extends Animal {
    protected static final ImmutableList<SensorType<? extends Sensor<? super GlassFrog>>> SENSOR_TYPES = ImmutableList.of(
            SensorType.NEAREST_LIVING_ENTITIES, SensorType.HURT_BY, SensorType.FROG_TEMPTATIONS, SensorType.IS_IN_WATER
    );
    protected static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(
            MemoryModuleType.LOOK_TARGET,
            MemoryModuleType.NEAREST_LIVING_ENTITIES,
            MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES,
            MemoryModuleType.WALK_TARGET,
            MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
            MemoryModuleType.PATH,
            MemoryModuleType.BREED_TARGET,
            MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS,
            MemoryModuleType.LONG_JUMP_MID_JUMP,
            MemoryModuleType.ATTACK_TARGET,
            MemoryModuleType.TEMPTING_PLAYER,
            MemoryModuleType.TEMPTATION_COOLDOWN_TICKS,
            MemoryModuleType.IS_TEMPTED,
            MemoryModuleType.HURT_BY,
            MemoryModuleType.HURT_BY_ENTITY,
            MemoryModuleType.NEAREST_ATTACKABLE,
            MemoryModuleType.IS_IN_WATER,
            MemoryModuleType.IS_PANICKING
    );

    public int jumpDelay = 0;

    public GlassFrog(EntityType<? extends GlassFrog> type, Level level) {
        super(type, level);
        this.setPathfindingMalus(PathType.WATER, 4.0F);
        this.setPathfindingMalus(PathType.TRAPDOOR, -1.0F);
        this.moveControl = new SmoothSwimmingMoveControl(this, 85, 10, 0.02F, 0.1F, true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createAnimalAttributes()
                .add(Attributes.MOVEMENT_SPEED, 2.5)
                .add(Attributes.MAX_HEALTH, 4.0)
                .add(Attributes.ATTACK_DAMAGE, 6.0)
                .add(Attributes.STEP_HEIGHT, 1.0);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(ItemTags.FROG_FOOD);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        GlassFrog frog = ExtraEntities.GLASS_FROG.create(level, EntitySpawnReason.BREEDING);
        if (frog != null) {
//            GlassFrogAi.initMemories(frog, level.getRandom()); // Todo 26.1 Port
        }

        return frog;
    }

    @Override
    public SpawnGroupData finalizeSpawn(
            ServerLevelAccessor levelAccessor, DifficultyInstance difficultyInstance, EntitySpawnReason spawnReason, @Nullable SpawnGroupData spawnGroupData
    ) {
//        GlassFrogAi.initMemories(this, levelAccessor.getRandom()); // Todo 26.1 Port
        return super.finalizeSpawn(levelAccessor, difficultyInstance, spawnReason, spawnGroupData);
    }

    @Override
    protected int calculateFallDamage(double fallDistance, float damageMultiplier) {
        return super.calculateFallDamage(fallDistance, damageMultiplier) - 5;
    }

    // Todo 26.1 Port

//    @Override
//    protected Brain.Provider<GlassFrog> brainProvider() {
//        return Brain.provider(MEMORY_TYPES, SENSOR_TYPES);
//    }
//
//    @Override
//    protected Brain<? extends LivingEntity> makeBrain(Brain.Packed packedBrain) {
//        return GlassFrogAi.makeBrain(this.brainProvider().makeBrain(packedBrain));;
//    }

//    @Override
//    public Brain<GlassFrog> getBrain() {
//        return (Brain<GlassFrog>) super.getBrain();
//    }

    @Override
    protected void customServerAiStep(ServerLevel level) {
//        this.getBrain().tick(level, this); // Todo 26.1 Port
//        GlassFrogAi.updateActivity(this);
        super.customServerAiStep(level);

        if ((!this.getNavigation().isDone() || this.getTarget() != null) && (this.onGround() || this.isInWater())) {
            if (this.jumpDelay > 0) {
                --this.jumpDelay;
            }

            if (this.jumpDelay <= 0) {
                this.jumpDelay = 5 + this.random.nextInt(4);
                Vec3 motion = this.getDeltaMovement();
                if (motion.horizontalDistanceSqr() > 4.0E-4) {
                    double motionY = motion.y + 0.3;
                    double motionX = motion.x * 1.1;
                    double motionZ = motion.z * 1.1;
                    this.setDeltaMovement(motionX, motionY, motionZ);
                }
            }
        }
    }

    @Override
    public int getHeadRotSpeed() {
        return 35;
    }

    @Override
    public int getMaxHeadYRot() {
        return 5;
    }

    @Override
    public void travel(Vec3 travelVector) {
        if (this.isInWater()) {
            this.moveRelative(this.getSpeed(), travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9));
        } else {
            super.travel(travelVector);
        }
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    static class GlassFrogNodeEvaluator extends AmphibiousNodeEvaluator {
        private final BlockPos.MutableBlockPos belowPos = new BlockPos.MutableBlockPos();

        public GlassFrogNodeEvaluator(boolean p_218548_) {
            super(p_218548_);
        }

        @Override
        public Node getStart() {
            return !this.mob.isInWater()
                    ? super.getStart()
                    : this.getStartNode(
                    new BlockPos(
                            Mth.floor(this.mob.getBoundingBox().minX), Mth.floor(this.mob.getBoundingBox().minY), Mth.floor(this.mob.getBoundingBox().minZ)
                    )
            );
        }

        @Override
        public PathType getPathType(PathfindingContext context, int x, int y, int z) {
            this.belowPos.set(x, y - 1, z);
            BlockState blockstate = context.getBlockState(this.belowPos);
            return blockstate.is(BlockTags.FROG_PREFER_JUMP_TO) ? PathType.OPEN : super.getPathType(context, x, y, z);
        }
    }

    static class GlassFrogPathNavigation extends AmphibiousPathNavigation {
        GlassFrogPathNavigation(Frog mob, Level level) {
            super(mob, level);
        }

        @Override
        public boolean canCutCorner(PathType pathType) {
            return pathType != PathType.WATER_BORDER && super.canCutCorner(pathType);
        }

        @Override
        protected PathFinder createPathFinder(int p_218559_) {
            this.nodeEvaluator = new GlassFrogNodeEvaluator(true);
            return new PathFinder(this.nodeEvaluator, p_218559_);
        }
    }
}
