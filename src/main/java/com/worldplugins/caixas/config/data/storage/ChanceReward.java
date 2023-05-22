package com.worldplugins.caixas.config.data.storage;

import com.worldplugins.lib.util.storage.item.StorableItem;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ChanceReward implements StorableItem {
    private final @NotNull ItemStack item;

    private final double chance;

    public ChanceReward(@NotNull ItemStack item, double chance) {
        this.item = item;
        this.chance = chance;
    }

    @Override
    public @NotNull ItemStack bukkitItem() {
        return item;
    }

    public double chance() {
        return chance;
    }
}
