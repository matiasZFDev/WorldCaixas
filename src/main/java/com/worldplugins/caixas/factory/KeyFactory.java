package com.worldplugins.caixas.factory;

import com.worldplugins.caixas.config.data.MainData;
import com.worldplugins.lib.config.cache.ConfigCache;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

@RequiredArgsConstructor
public class KeyFactory {
    private final @NonNull ConfigCache<MainData> mainConfig;

    public @NonNull Optional<ItemStack> create(@NonNull String id) {
        final Optional<MainData.Crate> crate = mainConfig.data().getCrates().getById(id);
        return crate.map(it -> it.getKeyItem().clone());
    }
}
