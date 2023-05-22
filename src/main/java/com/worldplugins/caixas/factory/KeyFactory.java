package com.worldplugins.caixas.factory;

import com.worldplugins.caixas.config.data.MainData;
import me.post.lib.config.model.ConfigModel;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class KeyFactory {
    private final @NotNull ConfigModel<MainData> mainConfig;

    public KeyFactory(@NotNull ConfigModel<MainData> mainConfig) {
        this.mainConfig = mainConfig;
    }

    public ItemStack create(@NotNull String id) {
        final MainData.Crate crate = mainConfig.data().crates().getById(id);
        return crate == null ? null : crate.getKeyItem().clone();
    }
}
