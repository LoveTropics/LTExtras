//Todo 26.1 Port
//package com.lovetropics.extras.placeholder;
//
//import eu.pb4.placeholders.api.PlaceholderResult;
//import eu.pb4.placeholders.api.Placeholders;
//import net.minecraft.resources.Identifier;
//import net.minecraft.server.ServerScoreboard;
//import net.minecraft.world.scores.Objective;
//import net.minecraft.world.scores.PlayerScoreEntry;
//import net.neoforged.fml.earlydisplay.util.Placeholders;
//
//import java.util.ArrayList;
//import java.util.Comparator;
//import java.util.List;
//
//// Stolen and "unreversed" from https://github.com/LoveTropics/TextPlaceholderAPI/blob/lt25/src/main/java/eu/pb4/placeholders/impl/placeholder/builtin/ServerPlaceholders.java
//public class ExtraPlaceholders {
//
//    public static void init() {
//        Placeholders.register(Identifier.fromNamespaceAndPath("server", "objective_name_bottom"), (ctx, arg) -> {
//            var args = arg.split(" ");
//            if (args.length >= 2) {
//                ServerScoreboard scoreboard = ctx.server().getScoreboard();
//                Objective scoreboardObjective = scoreboard.getObjective(args[0]);
//                if (scoreboardObjective == null) {
//                    return PlaceholderResult.invalid("Invalid objective!");
//                }
//                try {
//                    int position = Integer.parseInt(args[1]);
//                    List<PlayerScoreEntry> scoreboardEntries = new ArrayList<>(scoreboard.listPlayerScores(scoreboardObjective));
//                    scoreboardEntries.sort(Comparator.comparingInt(PlayerScoreEntry::value));
//
//                    PlayerScoreEntry scoreboardEntry = scoreboardEntries.get(position - 1);
//                    return PlaceholderResult.value(scoreboardEntry.ownerName());
//                } catch (Exception e) {
//                    /* Into the void you go! */
//                    return PlaceholderResult.invalid("Invalid position!");
//                }
//            }
//            return PlaceholderResult.invalid("Not enough arguments!");
//        });
//        Placeholders.register(Identifier.fromNamespaceAndPath("server", "objective_score_bottom"), (ctx, arg) -> {
//            var args = arg.split(" ");
//            if (args.length >= 2) {
//                ServerScoreboard scoreboard = ctx.server().getScoreboard();
//                Objective scoreboardObjective = scoreboard.getObjective(args[0]);
//                if (scoreboardObjective == null) {
//                    return PlaceholderResult.invalid("Invalid objective!");
//                }
//                try {
//                    int position = Integer.parseInt(args[1]);
//                    List<PlayerScoreEntry> scoreboardEntries = new ArrayList<>(scoreboard.listPlayerScores(scoreboardObjective));
//                    scoreboardEntries.sort(Comparator.comparingInt(PlayerScoreEntry::value));
//
//                    PlayerScoreEntry scoreboardEntry = scoreboardEntries.get(position - 1);
//                    return PlaceholderResult.value(String.valueOf(scoreboardEntry.value()));
//                } catch (Exception e) {
//                    /* Into the void you go! */
//                    return PlaceholderResult.invalid("Invalid position!");
//                }
//            }
//            return PlaceholderResult.invalid("Not enough arguments!");
//        });
//    }
//}
