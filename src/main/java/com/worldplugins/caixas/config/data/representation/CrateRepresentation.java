package com.worldplugins.caixas.config.data.representation;

import lombok.NonNull;
import org.bukkit.Location;

public interface CrateRepresentation {
    interface Handler {
        void remove();
    }

    @NonNull Handler spawn(@NonNull Location location);
}
