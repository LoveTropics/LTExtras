package com.lovetropics.extras.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ObjectiveArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.ReadOnlyScoreInfo;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Scoreboard;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.commands.Commands.literal;

public class ListScoreboardCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        // @formatter:off
        dispatcher.register(
                literal("listscoreboard").requires(Commands.hasPermission(Commands.LEVEL_OWNERS))
                .then(Commands.argument("objective", ObjectiveArgument.objective())
                   .then(literal("players")
                       .executes(ListScoreboardCommand::listPlayers)
                   )
                   .then(literal("scores")
                       .executes(ListScoreboardCommand::listScores)
                   )
                )
        );
        // @formatter:on
    }

    private static int listPlayers(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Objective objective = ObjectiveArgument.getObjective(ctx, "objective");
        Scoreboard scoreboard = objective.getScoreboard();
        List<String> names = new ArrayList<>();
        for (ScoreHolder trackedPlayer : scoreboard.getTrackedPlayers()) {
            ReadOnlyScoreInfo playerScoreInfo = scoreboard.getPlayerScoreInfo(trackedPlayer, objective);
            if (playerScoreInfo != null) {
                names.add(trackedPlayer.getScoreboardName());
            }
        }
        ctx.getSource().sendSuccess(() -> Component.literal("Players with score for ").append(objective.getDisplayName()).append(String.join(", ", names)), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int listScores(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Objective objective = ObjectiveArgument.getObjective(ctx, "objective");
        Scoreboard scoreboard = objective.getScoreboard();
        MutableComponent header = Component.literal("Scores for objective: ").append(objective.getDisplayName());
        for (ScoreHolder trackedPlayer : scoreboard.getTrackedPlayers()) {
            ReadOnlyScoreInfo playerScoreInfo = scoreboard.getPlayerScoreInfo(trackedPlayer, objective);
            if (playerScoreInfo != null) {
                int score = playerScoreInfo.value();
                MutableComponent scoreLine = Component.literal(trackedPlayer.getScoreboardName()).append(": ").append(Component.literal(Integer.toString(score)));
                header.append(Component.literal("\n"));
                header.append(scoreLine);
            }
        }
        ctx.getSource().sendSuccess(() -> header, false);
        return Command.SINGLE_SUCCESS;
    }
}
