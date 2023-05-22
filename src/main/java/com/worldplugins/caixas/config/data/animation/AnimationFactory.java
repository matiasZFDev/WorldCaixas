package com.worldplugins.caixas.config.data.animation;

import me.post.lib.util.Scheduler;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public interface AnimationFactory {
    @NotNull Animation create(@NotNull Scheduler scheduler, @NotNull Location origin);
}
