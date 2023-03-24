package com.worldplugins.caixas.config.data.representation;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;

@RequiredArgsConstructor
public class NormalBlock implements CrateRepresentation {
    private final Material material;
    private final byte data;

    @Override
    @SuppressWarnings("deprecation")
    public @NonNull CrateRepresentation.Handler spawn(@NonNull Location location) {
        location.getBlock().setType(material);
        location.getBlock().setData(data);
        return () -> {
            location.getBlock().setType(Material.AIR);
        };
    }
}
