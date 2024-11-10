package com.lovetropics.extras.mixin.placeholder;

import com.lovetropics.extras.network.message.ClientboundSetDisplayTextPacket;
import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.parsers.NodeParser;
import eu.pb4.placeholders.api.parsers.StaticPreParser;
import eu.pb4.placeholders.api.parsers.TextParserV1;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.stream.Collectors;

@Mixin(Display.TextDisplay.class)
public abstract class TextDisplayMixin extends Display {
	@Unique
	private static final int UPDATE_INTERVAL = SharedConstants.TICKS_PER_SECOND;

	@Unique
	private static final NodeParser TEXT_PARSER = NodeParser.merge(TextParserV1.DEFAULT, Placeholders.DEFAULT_PLACEHOLDER_PARSER, StaticPreParser.INSTANCE);

	@Unique
	@Nullable
	private String ltextras$templateText;
	@Unique
	@Nullable
	private TextNode ltextras$parsedTemplate;

	@Unique
	private final Map<ServerPlayer, Component> ltextras$trackingPlayers = new Reference2ObjectOpenHashMap<>();

	public TextDisplayMixin(EntityType<?> entityType, Level level) {
		super(entityType, level);
	}

	@Shadow
	protected abstract Component getText();

	@Override
	public void tick() {
		super.tick();
		if (!level().isClientSide() && !ltextras$trackingPlayers.isEmpty()) {
			if (tickCount % UPDATE_INTERVAL == 0) {
				ltextras$sendTextUpdatesToPlayers();
			}
		}
	}

	@Unique
	private void ltextras$sendTextUpdatesToPlayers() {
		for (Map.Entry<ServerPlayer, Component> entry : ltextras$trackingPlayers.entrySet()) {
			ServerPlayer player = entry.getKey();
			Component text = ltextras$resolveTextForPlayer(player);
			if (!text.equals(entry.getValue())) {
				entry.setValue(text);
				ltextras$sendTextToPlayer(player, text);
			}
		}
	}

	@Override
	public void startSeenByPlayer(ServerPlayer player) {
		super.startSeenByPlayer(player);
		Component text = ltextras$resolveTextForPlayer(player);
		ltextras$trackingPlayers.put(player, text);
		// Will be sent by normal data tracker entry if it's a regular text display
		if (!text.equals(getText())) {
			ltextras$sendTextToPlayer(player, text);
		}
	}

	@Override
	public void stopSeenByPlayer(ServerPlayer player) {
		super.stopSeenByPlayer(player);
		ltextras$trackingPlayers.remove(player);
	}

	@Unique
	private Component ltextras$resolveTextForPlayer(ServerPlayer player) {
		if (ltextras$parsedTemplate != null) {
			PlaceholderContext context = PlaceholderContext.of(player);
			return ltextras$parsedTemplate.toText(context);
		}
		return getText();
	}

	@Unique
	private void ltextras$sendTextToPlayer(ServerPlayer player, Component text) {
		PacketDistributor.sendToPlayer(player, new ClientboundSetDisplayTextPacket(getId(), text));
	}

	@Unique
	private void ltextras$setTemplateText(@Nullable String templateText) {
		ltextras$templateText = templateText;
		if (templateText != null) {
			ltextras$parsedTemplate = TEXT_PARSER.parseNode(templateText);
		}
	}

	@Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
	private void readAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
		if (tag.contains("template", Tag.TAG_LIST)) {
			ListTag lines = tag.getList("template", Tag.TAG_STRING);
			ltextras$setTemplateText(lines.stream().map(Tag::getAsString).collect(Collectors.joining("<r>\n")));
		} else if (tag.contains("template", Tag.TAG_STRING)) {
			ltextras$setTemplateText(tag.getString("template"));
		} else {
			ltextras$setTemplateText(null);
		}
	}

	@Inject(method = "addAdditionalSaveData", at = @At("RETURN"))
	private void addAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
		if (ltextras$templateText != null) {
			tag.putString("template", ltextras$templateText);
		}
	}
}
