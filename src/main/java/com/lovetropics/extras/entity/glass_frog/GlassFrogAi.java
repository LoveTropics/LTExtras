// Todo 26.1 Port
//package com.lovetropics.extras.entity.glass_frog;
//
//import com.google.common.collect.ImmutableList;
//import com.google.common.collect.ImmutableMap;
//import com.google.common.collect.ImmutableSet;
//import com.lovetropics.extras.entity.ExtraEntities;
//import com.mojang.datafixers.util.Pair;
//import net.minecraft.core.BlockPos;
//import net.minecraft.sounds.SoundEvents;
//import net.minecraft.tags.BlockTags;
//import net.minecraft.tags.ItemTags;
//import net.minecraft.util.RandomSource;
//import net.minecraft.util.valueproviders.UniformInt;
//import net.minecraft.world.entity.Entity;
//import net.minecraft.world.entity.EntityType;
//import net.minecraft.world.entity.Mob;
//import net.minecraft.world.entity.ai.Brain;
//import net.minecraft.world.entity.ai.behavior.*;
//import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
//import net.minecraft.world.entity.ai.memory.MemoryModuleType;
//import net.minecraft.world.entity.ai.memory.MemoryStatus;
//import net.minecraft.world.entity.schedule.Activity;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.level.block.state.BlockState;
//import net.minecraft.world.level.pathfinder.PathType;
//import net.minecraft.world.level.pathfinder.PathfindingContext;
//import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
//
//import java.util.function.Predicate;
//
//public class GlassFrogAi {
//    private static final UniformInt TIME_BETWEEN_LONG_JUMPS = UniformInt.of(100, 140);
//
//    protected static void initMemories(GlassFrog frog, RandomSource random) {
//        frog.getBrain().setMemory(MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS, TIME_BETWEEN_LONG_JUMPS.sample(random));
//    }
//
//    protected static Brain<?> makeBrain(Brain<GlassFrog> brain) {
//        initCoreActivity(brain);
//        initIdleActivity(brain);
//        initSwimActivity(brain);
//        initJumpActivity(brain);
//        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
//        brain.setDefaultActivity(Activity.IDLE);
//        brain.useDefaultActivity();
//        return brain;
//    }
//
//    private static void initCoreActivity(Brain<GlassFrog> brain) {
//        brain.addActivity(
//            Activity.CORE,
//            0,
//            ImmutableList.of(
//                new AnimalPanic<>(2.0F),
//                new LookAtTargetSink(45, 90),
//                new MoveToTargetSink(),
//                new CountDownCooldownTicks(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS),
//                new CountDownCooldownTicks(MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS)
//            )
//        );
//    }
//
//    private static void initIdleActivity(Brain<GlassFrog> brain) {
//        brain.addActivityWithConditions(
//            Activity.IDLE,
//            ImmutableList.of(
//                Pair.of(0, SetEntityLookTargetSometimes.create(EntityType.PLAYER, 6.0F, UniformInt.of(30, 60))),
//                Pair.of(0, new AnimalMakeLove(ExtraEntities.GLASS_FROG.get())),
//                Pair.of(1, new FollowTemptation(livingEntity -> 1.25F)),
//                Pair.of(
//                    2,
//                    StartAttacking.create(
//                        (level, frog) -> canAttack(frog),
//                        (level, frog) -> frog.getBrain().getMemory(MemoryModuleType.NEAREST_ATTACKABLE)
//                    )
//                ),
//                Pair.of(3, TryFindLand.create(6, 1.0F)),
//                Pair.of(
//                    4,
//                    new RunOne<>(
//                        ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT),
//                        ImmutableList.of(
//                            Pair.of(RandomStroll.stroll(1.0F), 1),
//                            Pair.of(SetWalkTargetFromLookTarget.create(1.0F, 3), 1),
//                            Pair.of(new GlassCroak(), 3),
//                            Pair.of(BehaviorBuilder.triggerIf(Entity::onGround), 2)
//                        )
//                    )
//                )
//            ),
//            ImmutableSet.of(
//                Pair.of(MemoryModuleType.LONG_JUMP_MID_JUMP, MemoryStatus.VALUE_ABSENT), Pair.of(MemoryModuleType.IS_IN_WATER, MemoryStatus.VALUE_ABSENT)
//            )
//        );
//    }
//
//    private static void initSwimActivity(Brain<GlassFrog> brain) {
//        brain.addActivityWithConditions(
//            Activity.SWIM,
//            ImmutableList.of(
//                Pair.of(0, SetEntityLookTargetSometimes.create(EntityType.PLAYER, 6.0F, UniformInt.of(30, 60))),
//                Pair.of(1, new FollowTemptation(livingEntity -> 1.25F)),
//                Pair.of(
//                    2,
//                    StartAttacking.create(
//                        (level, frog) -> canAttack(frog),
//                        (level, frog) -> frog.getBrain().getMemory(MemoryModuleType.NEAREST_ATTACKABLE)
//                    )
//                ),
//                Pair.of(3, TryFindLand.create(8, 1.5F)),
//                Pair.of(
//                    5,
//                    new GateBehavior<>(
//                        ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT),
//                        ImmutableSet.of(),
//                        GateBehavior.OrderPolicy.ORDERED,
//                        GateBehavior.RunningPolicy.TRY_ALL,
//                        ImmutableList.of(
//                            Pair.of(RandomStroll.swim(0.75F), 1),
//                            Pair.of(RandomStroll.stroll(1.0F, true), 1),
//                            Pair.of(SetWalkTargetFromLookTarget.create(1.0F, 3), 1),
//                            Pair.of(BehaviorBuilder.triggerIf(Entity::isInWater), 5)
//                        )
//                    )
//                )
//            ),
//            ImmutableSet.of(
//                Pair.of(MemoryModuleType.LONG_JUMP_MID_JUMP, MemoryStatus.VALUE_ABSENT), Pair.of(MemoryModuleType.IS_IN_WATER, MemoryStatus.VALUE_PRESENT)
//            )
//        );
//    }
//
//    private static void initJumpActivity(Brain<GlassFrog> brain) {
//        brain.addActivityWithConditions(
//            Activity.LONG_JUMP,
//            ImmutableList.of(
//                Pair.of(0, new LongJumpMidJump(TIME_BETWEEN_LONG_JUMPS, SoundEvents.FROG_STEP)),
//                Pair.of(
//                    1,
//                    new LongJumpToPreferredBlock<>(
//                        TIME_BETWEEN_LONG_JUMPS,
//                        2,
//                        4,
//                        3.5714288F,
//                        frog -> SoundEvents.FROG_LONG_JUMP,
//                        BlockTags.FROG_PREFER_JUMP_TO,
//                        0.5F,
//                        GlassFrogAi::isAcceptableLandingSpot
//                    )
//                )
//            ),
//            ImmutableSet.of(
//                Pair.of(MemoryModuleType.TEMPTING_PLAYER, MemoryStatus.VALUE_ABSENT),
//                Pair.of(MemoryModuleType.BREED_TARGET, MemoryStatus.VALUE_ABSENT),
//                Pair.of(MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS, MemoryStatus.VALUE_ABSENT),
//                Pair.of(MemoryModuleType.IS_IN_WATER, MemoryStatus.VALUE_ABSENT)
//            )
//        );
//    }
//
//    private static <E extends Mob> boolean isAcceptableLandingSpot(E mob, BlockPos pos) {
//        Level level = mob.level();
//        BlockPos blockpos = pos.below();
//        if (level.getFluidState(pos).isEmpty() && level.getFluidState(blockpos).isEmpty() && level.getFluidState(pos.above()).isEmpty()) {
//            BlockState blockstate = level.getBlockState(pos);
//            BlockState blockstate1 = level.getBlockState(blockpos);
//            if (!blockstate.is(BlockTags.FROG_PREFER_JUMP_TO) && !blockstate1.is(BlockTags.FROG_PREFER_JUMP_TO)) {
//                PathfindingContext pathfindingcontext = new PathfindingContext(mob.level(), mob);
//                PathType pathtype = WalkNodeEvaluator.getPathTypeStatic(pathfindingcontext, pos.mutable());
//                PathType pathtype1 = WalkNodeEvaluator.getPathTypeStatic(pathfindingcontext, blockpos.mutable());
//                return pathtype != PathType.TRAPDOOR && (!blockstate.isAir() || pathtype1 != PathType.TRAPDOOR)
//                    ? LongJumpToRandomPos.defaultAcceptableLandingSpot(mob, pos)
//                    : true;
//            } else {
//                return true;
//            }
//        } else {
//            return false;
//        }
//    }
//
//    private static boolean canAttack(GlassFrog frog) {
//        return !BehaviorUtils.isBreeding(frog);
//    }
//
//    public static void updateActivity(GlassFrog frog) {
//        frog.getBrain()
//            .setActiveActivityToFirstValid(ImmutableList.of(Activity.LONG_JUMP, Activity.SWIM, Activity.IDLE));
//    }
//
//    public static Predicate<ItemStack> getTemptations() {
//        return p_335267_ -> p_335267_.is(ItemTags.FROG_FOOD);
//    }
//}
