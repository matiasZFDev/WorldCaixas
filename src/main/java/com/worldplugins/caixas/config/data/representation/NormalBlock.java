package com.worldplugins.caixas.config.data.representation;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;

@RequiredArgsConstructor
public class NormalBlock implements MeasuredCrateRepresentation {
    private final Material material;
    private final byte data;

    @Override
    @SuppressWarnings("deprecation")
    public @NonNull CrateRepresentation.Handler spawn(@NonNull Plugin plugin, @NonNull Location location) {
        location.getBlock().setType(material);
        location.getBlock().setData(data);
        return () -> {
            location.getBlock().setType(Material.AIR);
        };
    }

    @Override
    public float size() {
        return 1f;
    }
}
