package com.worldplugins.caixas.config.data.animation;

import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

public interface AnimationFactory {
    @NonNull Animation create(@NonNull Plugin plugin, @NonNull Location origin);
}
