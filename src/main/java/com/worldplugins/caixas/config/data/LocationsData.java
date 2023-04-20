package com.worldplugins.caixas.config.data;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;

import java.util.Collection;

@RequiredArgsConstructor
@Getter
public class LocationsData {
    @RequiredArgsConstructor
    @Getter
    public static class CrateLocation {
        private final @NonNull String crateId;
        private final @NonNull Location location;
    }

    private final @NonNull Collection<CrateLocation> locations;
}
