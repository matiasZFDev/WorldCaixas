package com.worldplugins.caixas.config.data.representation;

import org.bukkit.Location;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public class NormalBlock implements MeasuredCrateRepresentation {
    private final @NotNull Material material;
    private final byte data;

    public NormalBlock(@NotNull Material material, byte data) {
        this.material = material;
        this.data = data;
    }

    @Override
    @SuppressWarnings("deprecation")
    public @NotNull CrateRepresentation.@NotNull Handler spawn(@NotNull Location location) {
        location.getBlock().setType(material);
        location.getBlock().setData(data);
        return () -> location.getBlock().setType(Material.AIR);
    }

    @Override
    public float size() {
        return 1f;
    }
}
