package com.worldplugins.caixas.rewards;

import com.worldplugins.lib.api.storage.item.StorableItem;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public class ChanceReward implements StorableItem {
    private final @NonNull ItemStack item;
    @Getter
    private final double chance;

    @Override
    public ItemStack getBukkitItem() {
        return item;
    }
}
