package com.lovetropics.extras.command;

import com.lovetropics.extras.ExtraDataComponents;
import com.lovetropics.extras.ExtraItems;
import com.lovetropics.extras.hats.StackedHatStore;
import com.lovetropics.lib.item.StackedHat;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityAttachments;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

@EventBusSubscriber
public class HatCommand {
    private static final SimpleCommandExceptionType NO_HATS = new SimpleCommandExceptionType(Component.literal("The entity has no hat."));

    private static LivingEntity getLivingEntity(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity entity = context.getSource().getEntityOrException();
        if (!(entity instanceof LivingEntity living)) {
            throw NO_HATS.create();
        }
        return living;
    }

    private static Collection<LivingEntity> getLivingEntities(CommandContext<CommandSourceStack> context, String key) throws CommandSyntaxException {
        Collection<? extends Entity> entities = EntityArgument.getEntities(context, key);
        List<LivingEntity> livingEntities = new ArrayList<>();
        for (Entity entity : entities) {
            if (entity instanceof LivingEntity living) {
                livingEntities.add(living);
            }
        }
        return livingEntities;
    }

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(literal("hat")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(literal("item")
                    .then(literal("add")
                        .then(argument("item", ItemArgument.item(event.getBuildContext()))
                            .executes(c -> {
                                ItemStack newItem = ItemArgument.getItem(c, "item").createItemStack(1);
                                addHat(getLivingEntity(c), new StackedHat(ItemStackTemplate.fromNonEmptyStack(newItem), 1.0f, 1.0f, 1.0f));
                                return Command.SINGLE_SUCCESS;
                            })
                            .then(argument("width", FloatArgumentType.floatArg(0.01f, 10.0f))
                                .then(argument("height", FloatArgumentType.floatArg(0.01f, 10.0f))
                                    .then(argument("bounciness", FloatArgumentType.floatArg(0.01f, 10.0f))
                                        .executes(c -> {
                                            ItemStack newItem = ItemArgument.getItem(c, "item").createItemStack(1);
                                            float width = FloatArgumentType.getFloat(c, "width");
                                            float height = FloatArgumentType.getFloat(c, "height");
                                            float bounciness = FloatArgumentType.getFloat(c, "bounciness");
                                            addHat(getLivingEntity(c), new StackedHat(ItemStackTemplate.fromNonEmptyStack(newItem), width, height, bounciness));
                                            return Command.SINGLE_SUCCESS;
                                        })
                                    )
                                )
                            )
                        )
                    )
                    .then(literal("remove")
                        .then(literal("top")
                            .executes(c -> {
                                 removeHat(getLivingEntity(c), true);
                                 return Command.SINGLE_SUCCESS;
                            })
                        )
                        .then(literal("bottom")
                            .executes(c -> {
                                removeHat(getLivingEntity(c), false);
                                return Command.SINGLE_SUCCESS;
                            })
                        )
                    )
                )
                .then(literal("entity")
                    .then(argument("entities", EntityArgument.entities())
                        .then(literal("add")
                            .then(argument("item", ItemArgument.item(event.getBuildContext()))
                                .executes(context -> {
                                    ItemStack newItem = ItemArgument.getItem(context, "item").createItemStack(1);
                                    return addEntityHat(context, newItem, 1.0f, 1.0f, 1.0f);
                                })
                                .then(argument("width", FloatArgumentType.floatArg(0.01f, 10.0f))
                                    .then(argument("height", FloatArgumentType.floatArg(0.01f, 10.0f))
                                        .then(argument("bounciness", FloatArgumentType.floatArg(0.01f, 10.0f))
                                            .executes(context -> {
                                                 ItemStack newItem = ItemArgument.getItem(context, "item").createItemStack(1);
                                                 float width = FloatArgumentType.getFloat(context, "width");
                                                 float height = FloatArgumentType.getFloat(context, "height");
                                                 float bounciness = FloatArgumentType.getFloat(context, "bounciness");
                                                 return addEntityHat(context, newItem, width, height, bounciness);
                                            })
                                        )
                                    )
                                )
                            )
                        )
                        .then(literal("remove")
                            .then(literal("top")
                                .executes(context -> removeEntityHat(context, true))
                            )
                            .then(literal("bottom")
                                .executes(context -> removeEntityHat(context, false))
                            )
                        )
                        .then(literal("clear")
                            .executes(HatCommand::clearEntitiesHats)
                        )
                    )
                )
        );
    }

    private static void addHat(LivingEntity living, StackedHat newHat) {
        ItemStack oldItem = living.getItemBySlot(EquipmentSlot.HEAD);

        ItemStack newItem;
        if (oldItem.is(ExtraItems.STACK_OF_HATS)) {
            newItem = oldItem.copy();
            newItem.update(ExtraDataComponents.HAT_STACK.get(), List.of(), hats -> Util.copyAndAdd(hats, newHat));
        } else {
            newItem = new ItemStack(ExtraItems.STACK_OF_HATS.get(), 1);
            if (oldItem.isEmpty()) {
                newItem.set(ExtraDataComponents.HAT_STACK, List.of(newHat));
            } else {
                newItem.set(ExtraDataComponents.HAT_STACK, List.of(
                        new StackedHat(ItemStackTemplate.fromNonEmptyStack(oldItem), 1.0f, 1.0f, 1.0f),
                        newHat
                ));
            }
        }
        living.setItemSlot(EquipmentSlot.HEAD, newItem);
    }

    private static void removeHat(LivingEntity living, boolean top) throws CommandSyntaxException {
        ItemStack oldItem = living.getItemBySlot(EquipmentSlot.HEAD);
        if (!oldItem.is(ExtraItems.STACK_OF_HATS)) {
            throw NO_HATS.create();
        }

        ItemStack newItem = oldItem.copy();
        newItem.update(ExtraDataComponents.HAT_STACK.get(), List.of(), hats -> {
            if (hats.isEmpty()) {
                return List.of();
            }
            List<StackedHat> newHats = new ArrayList<>(hats);
            newHats.remove(top ? newHats.size() - 1 : 0);
            return newHats;
        });
        living.setItemSlot(EquipmentSlot.HEAD, newItem);
    }

    private static int removeEntityHat(CommandContext<CommandSourceStack> context, boolean top) throws CommandSyntaxException {
        Collection<LivingEntity> entities = getLivingEntities(context, "entities");
        entities.forEach(entity -> {
            List<StackedHat> hats = StackedHatStore.getHats(entity);
            if (hats.isEmpty()) {
                return;
            }
            hats.remove(top ? hats.size() - 1 : 0);
        });
        return entities.size();
    }

    private static int addEntityHat(CommandContext<CommandSourceStack> context, ItemStack stack, float width, float height, float bounciness) throws CommandSyntaxException {
        Collection<LivingEntity> entities = getLivingEntities(context, "entities");
        entities.forEach(entity -> {
            StackedHat newHat = new StackedHat(ItemStackTemplate.fromNonEmptyStack(stack), width, height, bounciness);
            StackedHatStore.addHat(entity, newHat);
        });
        return entities.size();
    }

    private static int clearEntitiesHats(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Collection<LivingEntity> entities = getLivingEntities(context, "entities");
        entities.forEach(StackedHatStore::clearHats);
        return entities.size();
    }
}
