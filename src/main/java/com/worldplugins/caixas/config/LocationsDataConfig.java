package com.worldplugins.caixas.config;

import com.worldplugins.caixas.config.data.LocationsData;
import me.post.lib.config.model.ConfigModel;
import me.post.lib.config.wrapper.ConfigWrapper;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.stream.Collectors;

public class LocationsDataConfig implements ConfigModel<LocationsData> {
    private @UnknownNullability LocationsData data;
    private final @NotNull ConfigWrapper configWrapper;

    public LocationsDataConfig(@NotNull ConfigWrapper configWrapper) {
        this.configWrapper = configWrapper;
    }

    @Override
    public void update() {
        final FileConfiguration config = configWrapper.unwrap();
        final ConfigurationSection section = config.getConfigurationSection("Data");
        data = new LocationsData(
            section.getKeys(false).stream()
                .map(key -> new LocationsData.CrateLocation(key, (Location) section.get(key)))
                .collect(Collectors.toList())
        );
    }

    @Override
    public @NotNull LocationsData data() {
        return data;
    }

    @Override
    public @NotNull ConfigWrapper wrapper() {
        return configWrapper;
    }
}