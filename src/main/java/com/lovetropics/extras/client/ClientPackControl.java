package com.lovetropics.extras.client;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.lovetropics.extras.data.packcontrol.PackControl;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.neoforged.fml.loading.FMLLoader;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class ClientPackControl {
	private static final Logger LOGGER = LogUtils.getLogger();

	private static final Path STATE_PATH = FMLLoader.getGamePath().resolve("config/client_pack_control.json");
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	private static CompletableFuture<PackControl.State> stateFuture;

	static {
		stateFuture = CompletableFuture.supplyAsync(ClientPackControl::readState).exceptionally(throwable -> {
			LOGGER.error("Failed to read pack control state", throwable);
			return PackControl.State.DEFAULT;
		});
	}

	public static void updatePacks(PackControl.State state) {
		PackControl.State oldState = state();
		if (oldState.equals(state)) {
			return;
		}
		handleStateChange(Minecraft.getInstance(), oldState, state);
		stateFuture = CompletableFuture.completedFuture(state);
		Util.ioPool().submit(() -> storeState(state));
	}

	private static void handleStateChange(Minecraft minecraft, PackControl.State oldState, PackControl.State newState) {
		PackRepository packRepository = minecraft.getResourcePackRepository();

		Set<String> newlyEnabled = Sets.difference(newState.enabled(), oldState.enabled());
		Set<String> newlyDisabled = Sets.difference(oldState.enabled(), newState.enabled());

		newlyEnabled.forEach(packRepository::addPack);
		newlyDisabled.forEach(packRepository::removePack);

		minecraft.options.updateResourcePacks(packRepository);
	}

	private static PackControl.State readState() {
		if (!Files.exists(STATE_PATH)) {
			return PackControl.State.DEFAULT;
		}
		try (BufferedReader reader = Files.newBufferedReader(STATE_PATH)) {
			return PackControl.State.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseReader(reader)).getOrThrow();
		} catch (IOException e) {
			throw new CompletionException(e);
		}
	}

	private static void storeState(PackControl.State state) {
		try (BufferedWriter writer = Files.newBufferedWriter(STATE_PATH)) {
			JsonElement json = PackControl.State.CODEC.encodeStart(JsonOps.INSTANCE, state).getOrThrow();
			GSON.toJson(json, writer);
		} catch (Exception e) {
			LOGGER.error("Failed to write pack control state", e);
		}
	}

	public static PackControl.State state() {
		return stateFuture.join();
	}

	public static Collection<Pack> removeHidden(Collection<Pack> packs, PackRepository repository) {
		PackControl.State state = state();
		if (state.hidden().isEmpty()) {
			return packs;
		}
		List<Pack> newPacks = new ArrayList<>(packs);
		Collection<String> selectedIds = repository.getSelectedIds();
		if (newPacks.removeIf(pack -> {
			// Let the player keep it if they already had it enabled
			String id = pack.getId();
			return state.hidden().contains(id) && !selectedIds.contains(id);
		})) {
			return newPacks;
		}
		return packs;
	}
}
