package com.worldplugins.caixas.factory;

import com.worldplugins.caixas.config.data.MainData;
import com.worldplugins.lib.config.cache.ConfigCache;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public class KeyFactory {
    private final @NonNull ConfigCache<MainData> mainConfig;

    public ItemStack create(@NonNull String id) {
        final MainData.Crate crate = mainConfig.data().getCrates().getById(id);
        return crate == null ? null : crate.getKeyItem().clone();
    }
}
