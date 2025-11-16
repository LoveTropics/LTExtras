package com.lovetropics.extras.techstack;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.lovetropics.extras.LTExtras;
import com.lovetropics.lib.slideshow.SlideshowApi;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.LenientJsonParser;
import net.minecraft.util.thread.ConsecutiveExecutor;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class VideoImporter {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final VideoImporter INSTANCE = new VideoImporter();

    private final Path storeFile;
    private final List<ImportedVideo> importedVideos = new ArrayList<>();
    private final ConsecutiveExecutor executor = new ConsecutiveExecutor(Util.ioPool(), "lt-video-importer");

    private VideoImporter() {
        storeFile = FMLPaths.MODSDIR.get().resolve("lovetropics").resolve("imported_videos.json");
        executor.schedule(() -> {
            if (!Files.exists(storeFile)) {
                return;
            }
            try (BufferedReader reader = Files.newBufferedReader(storeFile)) {
                JsonElement json = LenientJsonParser.parse(reader);
                importedVideos.addAll(ImportedVideo.CODEC.listOf().parse(JsonOps.INSTANCE, json).getOrThrow(JsonSyntaxException::new));
            } catch (IOException | JsonSyntaxException | JsonIOException e) {
                LOGGER.error("Failed to load imported videos", e);
            }
        });
    }

    public static VideoImporter get() {
        return INSTANCE;
    }

    public void onServerStarted(ServerStartedEvent event) {
        MinecraftServer server = event.getServer();
        executor.schedule(() -> {
            List<ImportedVideo> importedVideos = List.copyOf(this.importedVideos);
            server.execute(() -> {
                for (ImportedVideo video : importedVideos) {
                    SlideshowApi.importSimpleVideo(video.id, video.url, video.duration);
                }
            });
        });
    }

    public void importVideo(String title, URI url, double duration) {
        ResourceLocation id = toVideoId(title);
        LOGGER.info("Importing video with id {} from {} and duration of {} seconds", id, url, duration);

        SlideshowApi.importSimpleVideo(id, url, duration);

        executor.schedule(() -> {
            importedVideos.add(new ImportedVideo(id, url, duration));
            try {
                Files.createDirectories(storeFile.getParent());
                try (BufferedWriter writer = Files.newBufferedWriter(storeFile, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
                    JsonElement json = ImportedVideo.CODEC.listOf().encodeStart(JsonOps.INSTANCE, importedVideos).getOrThrow();
                    GSON.toJson(json, writer);
                }
            } catch (IOException e) {
                LOGGER.error("Failed to write imported videos", e);
            }
        });
    }

    private static ResourceLocation toVideoId(String title) {
        StringBuilder id = new StringBuilder();
        title.chars().forEach(c -> {
            char ch = Character.toLowerCase((char) c);
            if (Character.isLetterOrDigit(ch)) {
                id.append(ch);
            } else {
                id.append('_');
            }
        });
        return LTExtras.location(id.toString());
    }

    private record ImportedVideo(
            ResourceLocation id,
            URI url,
            double duration
    ) {
        public static final Codec<ImportedVideo> CODEC = RecordCodecBuilder.create(i -> i.group(
                ResourceLocation.CODEC.fieldOf("id").forGetter(ImportedVideo::id),
                ExtraCodecs.UNTRUSTED_URI.fieldOf("url").forGetter(ImportedVideo::url),
                Codec.DOUBLE.fieldOf("duration").forGetter(ImportedVideo::duration)
        ).apply(i, ImportedVideo::new));
    }
}
