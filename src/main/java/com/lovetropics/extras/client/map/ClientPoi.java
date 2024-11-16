package com.lovetropics.extras.client.map;

import com.lovetropics.extras.data.poi.MapConfig;
import com.lovetropics.extras.data.poi.PoiConfig;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;

import java.util.List;
import java.util.UUID;

public class ClientPoi {
	private final ResourceKey<PoiConfig> id;
	private final Holder<MapConfig> map;
	private final Component description;
	private final PoiConfig.Icon icon;
	private final int markerX;
	private final int markerY;
	private List<UUID> faces = List.of();

	public ClientPoi(ResourceKey<PoiConfig> id, Holder<MapConfig> map, Component description, PoiConfig.Icon icon, int markerX, int markerY) {
		this.id = id;
		this.map = map;
		this.description = description;
		this.icon = icon;
		this.markerX = markerX;
		this.markerY = markerY;
	}

	public ResourceKey<PoiConfig> id() {
		return id;
	}

	public Holder<MapConfig> map() {
		return map;
	}

	public Component description() {
		return description;
	}

	public PoiConfig.Icon icon() {
		return icon;
	}

	public int markerX() {
		return markerX;
	}

	public int markerY() {
		return markerY;
	}

	public void updateFaces(List<UUID> faces) {
		this.faces = faces;
	}

	public List<UUID> faces() {
		return faces;
	}
}
