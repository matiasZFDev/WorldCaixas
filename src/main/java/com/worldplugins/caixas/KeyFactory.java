package com.worldplugins.caixas;

import com.worldplugins.caixas.config.MainConfig;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

@RequiredArgsConstructor
public class KeyFactory {
    private final @NonNull MainConfig mainConfig;

    public @NonNull Optional<ItemStack> create(@NonNull String id) {
        final Optional<MainConfig.Config.Crate> crate = mainConfig.get().getCrates().getById(id);
        return crate.map(it -> it.getKeyItem().clone());
    }
}
