package com.worldplugins.caixas.config;

import com.worldplugins.lib.common.Logger;
import com.worldplugins.lib.config.bukkit.ConfigContainer;
import com.worldplugins.lib.config.cache.StateConfig;
import com.worldplugins.lib.config.cache.annotation.Config;
import com.worldplugins.lib.extension.bukkit.ConfigurationExtensions;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Collection;
import java.util.stream.Collectors;

@ExtensionMethod({
    ConfigurationExtensions.class
})

@Config(path = "localizacoes")
public class LocationsDataConfig extends StateConfig<LocationsDataConfig.Config> {

    public LocationsDataConfig(Logger logger, @NonNull ConfigContainer configContainer) {
        super(logger, configContainer);
    }

    @RequiredArgsConstructor
    public static class Config {
        @RequiredArgsConstructor
        @Getter
        public static class CrateLocation {
            private final @NonNull String crateId;
            private final @NonNull Location location;
        }

        @Getter
        private final @NonNull Collection<CrateLocation> locations;
    }

    @Override
    public @NonNull Config fetch(@NonNull FileConfiguration config) {
        final ConfigurationSection section = config.getConfigurationSection("Data");
        return new Config(
            section.getKeys(false).stream()
                .map(key -> new Config.CrateLocation(key, (Location) section.get(key)))
                .collect(Collectors.toList())
        );
    }
}