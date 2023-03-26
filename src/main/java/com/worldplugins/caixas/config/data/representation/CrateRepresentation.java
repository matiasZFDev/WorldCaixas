package com.worldplugins.caixas.config.data.representation;

import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

public interface CrateRepresentation {
    interface Handler {
        void remove();
    }

    @NonNull Handler spawn(@NonNull Plugin plugin, @NonNull Location location);
}
