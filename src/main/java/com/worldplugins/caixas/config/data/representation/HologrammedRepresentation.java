package com.worldplugins.caixas.config.data.representation;

import com.worldplugins.caixas.util.HologramBuilder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import java.util.List;

@RequiredArgsConstructor
public class HologrammedRepresentation implements CrateRepresentation {
    private final @NonNull List<String> lines;
    private final @NonNull MeasuredCrateRepresentation representation;

    @Override
    public @NonNull Handler spawn(@NonNull Plugin plugin, @NonNull Location location) {
        final Hologram hologram = HologramBuilder.of()
            .appendLine(lines)
            .appendType(HologramBuilder.AppendType.ORIGIN_TO_BOTTOM)
            .build(plugin, location.clone().add(0.5, representation.size() + 1.5, 0.5));
        final Handler handler = representation.spawn(plugin, location);
        return () -> {
            hologram.delete();
            handler.remove();
        };
    }
}
