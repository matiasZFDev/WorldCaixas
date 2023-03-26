package com.worldplugins.caixas.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HologramBuilder {
    public enum AppendType {
        ORIGIN_TO_BOTTOM {
            @Override
            public @NonNull Hologram create(
                @NonNull HolographicDisplaysAPI api,
                @NonNull Location location,
                @NonNull List<String> lines
            ) {
                final Hologram hologram = api.createHologram(location);
                lines.forEach(line -> hologram.getLines().appendText(line));
                return hologram;
            }
        },
        ORIGIN_FLOOR {
            @Override
            public @NonNull Hologram create(
                @NonNull HolographicDisplaysAPI api,
                @NonNull Location location,
                @NonNull List<String> lines
            ) {
                final Hologram hologram = api.createHologram(location.clone().add(0d, lines.size() * 0.15d, 0d));
                lines.forEach(line -> hologram.getLines().appendText(line));
                return hologram;
            }
        };

        public abstract @NonNull Hologram create(
            @NonNull HolographicDisplaysAPI api,
            @NonNull Location location,
            @NonNull List<String> lines
        );
    }

    public static @NonNull HologramBuilder of() {
        return new HologramBuilder();
    }

    private AppendType appendType = AppendType.ORIGIN_FLOOR;
    private final List<String> lines = new ArrayList<>();

    public @NonNull HologramBuilder appendType(AppendType appendType) {
        this.appendType = appendType;
        return this;
    }

    public @NonNull HologramBuilder appendLine(@NonNull String line) {
        this.lines.add(line);
        return this;
    }

    public @NonNull HologramBuilder appendLine(@NonNull String... lines) {
        return this.appendLine(Arrays.asList(lines));
    }

    public @NonNull HologramBuilder appendLine(@NonNull List<String> lines) {
        this.lines.addAll(lines);
        return this;
    }

    public @NonNull Hologram build(@NonNull Plugin plugin, @NonNull Location location) {
        return appendType.create(HolographicDisplaysAPI.get(plugin), location, lines);
    }
}