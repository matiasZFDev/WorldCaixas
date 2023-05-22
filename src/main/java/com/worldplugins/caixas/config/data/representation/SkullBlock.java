package com.worldplugins.caixas.config.data.representation;

import com.worldplugins.lib.config.common.BlockData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SkullBlock implements MeasuredCrateRepresentation {
    private final @NotNull ItemStack head;

    public SkullBlock(@NotNull ItemStack head) {
        this.head = head;
    }

    @Override
    public @NotNull CrateRepresentation.Handler spawn(@NotNull Location location) {
        final BlockData block = new BlockData(head);
        block.set(location);
        return () -> location.getBlock().setType(Material.AIR);
    }

    @Override
    public float size() {
        return 0.5f;
    }
}
