package com.worldplugins.caixas.config;

import com.worldplugins.caixas.config.data.LocationsData;
import com.worldplugins.lib.config.cache.InjectedConfigCache;
import com.worldplugins.lib.config.cache.annotation.ConfigSpec;
import com.worldplugins.lib.extension.bukkit.ConfigurationExtensions;
import lombok.NonNull;
import lombok.experimental.ExtensionMethod;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.stream.Collectors;

@ExtensionMethod({
    ConfigurationExtensions.class
})

public class LocationsDataConfig implements InjectedConfigCache<LocationsData> {
    @ConfigSpec(path = "localizacoes")
    public @NonNull LocationsData transform(@NonNull FileConfiguration config) {
        final ConfigurationSection section = config.getConfigurationSection("Data");
        return new LocationsData(
            section.getKeys(false).stream()
                .map(key -> new LocationsData.CrateLocation(key, (Location) section.get(key)))
                .collect(Collectors.toList())
        );
    }
}