package com.worldplugins.caixas.util;

import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HologramBuilder {
    private AppendType appendType;
    private final List<String> lines;

    public static @NotNull HologramBuilder of() {
        return new HologramBuilder();
    }

    public @NotNull HologramBuilder appendType(AppendType appendType) {
        this.appendType = appendType;
        return this;
    }

    public @NotNull HologramBuilder appendLine(@NotNull String line) {
        this.lines.add(line);
        return this;
    }

    public @NotNull HologramBuilder appendLine(@NotNull String... lines) {
        return this.appendLine(Arrays.asList(lines));
    }

    public @NotNull HologramBuilder appendLine(@NotNull List<String> lines) {
        this.lines.addAll(lines);
        return this;
    }

    public @NotNull Hologram build(@NotNull Plugin plugin, @NotNull Location location) {
        return this.appendType.create(HolographicDisplaysAPI.get(plugin), location, this.lines);
    }

    private HologramBuilder() {
        this.appendType = AppendType.ORIGIN_FLOOR;
        this.lines = new ArrayList<>();
    }

    public enum AppendType {
        ORIGIN_TO_BOTTOM {
            public @NotNull Hologram create(
                @NotNull HolographicDisplaysAPI api,
                @NotNull Location location,
                @NotNull List<String> lines
            ) {
                Hologram hologram = api.createHologram(location);
                lines.forEach((line) ->
                    hologram.getLines().appendText(line)
                );
                return hologram;
            }
        },
        ORIGIN_FLOOR {
            public @NotNull Hologram create(
                @NotNull HolographicDisplaysAPI api,
                @NotNull Location location,
                @NotNull List<String> lines
            ) {
                Hologram hologram = api.createHologram(location.clone().add(
                    0.0D,
                    (double)lines.size() * 0.15D,
                    0.0D
                ));
                lines.forEach((line) ->
                    hologram.getLines().appendText(line)
                );
                return hologram;
            }
        };

        public abstract @NotNull Hologram create(
            @NotNull HolographicDisplaysAPI api,
            @NotNull Location location,
            @NotNull List<String> lines
        );
    }
}
