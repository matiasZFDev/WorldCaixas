package com.worldplugins.caixas.config.data.representation;

import com.worldplugins.caixas.util.HologramBuilder;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HologrammedRepresentation implements CrateRepresentation {
    private final @NotNull Plugin plugin;
    private final @NotNull List<String> lines;
    private final @NotNull MeasuredCrateRepresentation representation;

    public HologrammedRepresentation(
        @NotNull Plugin plugin,
        @NotNull List<String> lines,
        @NotNull MeasuredCrateRepresentation representation
    ) {
        this.plugin = plugin;
        this.lines = lines;
        this.representation = representation;
    }

    @Override
    public @NotNull Handler spawn(@NotNull Location location) {
        final Hologram hologram = HologramBuilder.of()
            .appendLine(lines)
            .appendType(HologramBuilder.AppendType.ORIGIN_TO_BOTTOM)
            .build(plugin, location.clone().add(0.5, representation.size() + 1.5, 0.5));
        final Handler handler = representation.spawn(location);
        return () -> {
            hologram.delete();
            handler.remove();
        };
    }
}
