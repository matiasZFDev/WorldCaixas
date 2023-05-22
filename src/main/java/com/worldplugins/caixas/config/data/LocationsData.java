package com.worldplugins.caixas.config.data;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class LocationsData {
    public static class CrateLocation {
        private final @NotNull String crateId;
        private final @NotNull Location location;

        public CrateLocation(@NotNull String crateId, @NotNull Location location) {
            this.crateId = crateId;
            this.location = location;
        }

        public @NotNull String crateId() {
            return crateId;
        }

        public @NotNull Location location() {
            return location;
        }
    }

    private final @NotNull Collection<CrateLocation> locations;

    public LocationsData(@NotNull Collection<CrateLocation> locations) {
        this.locations = locations;
    }

    public @NotNull Collection<CrateLocation> locations() {
        return locations;
    }
}
