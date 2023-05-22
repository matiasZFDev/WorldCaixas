package com.worldplugins.caixas.config.data.representation;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public interface CrateRepresentation {
    interface Handler {
        void remove();
    }

    @NotNull Handler spawn(@NotNull Location location);
}
