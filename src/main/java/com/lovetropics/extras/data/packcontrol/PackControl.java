package com.lovetropics.extras.data.packcontrol;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.network.message.ClientboundUpdatePackControl;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

@EventBusSubscriber(modid = LTExtras.MODID)
public class PackControl extends SavedData {
	private static final Factory<PackControl> FACTORY = new Factory<>(PackControl::new, PackControl::load);

	private static final String STORAGE_ID = LTExtras.MODID + "_pack_control";

	private State state = State.DEFAULT;

	@Override
	public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
		tag.put("state", State.CODEC.encodeStart(NbtOps.INSTANCE, state).getOrThrow());
		return tag;
	}

	public static PackControl get(MinecraftServer server) {
		return server.overworld().getDataStorage().computeIfAbsent(FACTORY, STORAGE_ID);
	}

	private static PackControl load(CompoundTag tag, HolderLookup.Provider registries) {
		PackControl packControl = new PackControl();
		State.CODEC.parse(NbtOps.INSTANCE, tag.get("state")).ifSuccess(state -> packControl.state = state);
		return packControl;
	}

	@SubscribeEvent
	public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
		if (event.getEntity() instanceof ServerPlayer player) {
			PackControl packControl = PackControl.get(player.getServer());
			player.connection.send(new ClientboundUpdatePackControl(packControl.state));
		}
	}

	@SubscribeEvent
	public static void onRegisterCommands(RegisterCommandsEvent event) {
		event.getDispatcher().register(literal("packcontrol")
				.requires(source -> source.hasPermission(Commands.LEVEL_GAMEMASTERS))
				.then(packUpdater("enable", (state, packId) -> state.setEnabled(packId, true)))
				.then(packUpdater("disable", (state, packId) -> state.setEnabled(packId, false)))
				.then(packUpdater("hide", (state, packId) -> state.setHidden(packId, true)))
				.then(packUpdater("show", (state, packId) -> state.setHidden(packId, false)))
		);
	}

	private static LiteralArgumentBuilder<CommandSourceStack> packUpdater(String name, BiFunction<State, String, State> updater) {
		return literal(name)
				.then(argument("pack", string())
						.executes(context -> {
							String pack = getString(context, "pack");
							updateState(context.getSource().getServer(), state1 -> updater.apply(state1, pack));
							return 1;
						})
				);
	}

	public static void updateState(MinecraftServer server, UnaryOperator<State> operator) {
		PackControl packControl = PackControl.get(server);
		State newState = operator.apply(packControl.state);
		if (packControl.state.equals(newState)) {
			return;
		}
		packControl.state = newState;
		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			player.connection.send(new ClientboundUpdatePackControl(newState));
		}
		packControl.setDirty();
	}

	public record State(Set<String> hidden, Set<String> enabled) {
		public static final State DEFAULT = new State(Set.of(), Set.of());

		private static final Codec<Set<String>> PACK_SET_CODEC = Codec.STRING.listOf().xmap(Set::copyOf, List::copyOf);

		public static final Codec<State> CODEC = RecordCodecBuilder.create(i -> i.group(
				PACK_SET_CODEC.fieldOf("hidden").forGetter(State::hidden),
				PACK_SET_CODEC.fieldOf("enabled").forGetter(State::enabled)
		).apply(i, State::new));

		private static final StreamCodec<ByteBuf, Set<String>> PACK_SET_STREAM_CODEC = ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.collection(HashSet::new));

		public static final StreamCodec<ByteBuf, State> STREAM_CODEC = StreamCodec.composite(
				PACK_SET_STREAM_CODEC, State::hidden,
				PACK_SET_STREAM_CODEC, State::enabled,
				State::new
		);

		public State setHidden(String packId, boolean hidden) {
			return new State(setInSet(this.hidden, packId, hidden), enabled);
		}

		public State setEnabled(String packId, boolean enabled) {
			return new State(this.hidden, setInSet(this.enabled, packId, enabled));
		}

		private static <T> Set<T> setInSet(Set<T> set, T value, boolean inSet) {
			Set<T> newSet = new HashSet<>(set);
			if (inSet) {
				newSet.add(value);
			} else {
				newSet.remove(value);
			}
			return newSet;
		}
	}
}
