package com.worldplugins.caixas.config.data.animation;

import lombok.NonNull;
import org.bukkit.Location;

public interface AnimationFactory {
    @NonNull Animation create(@NonNull Location origin);
}
