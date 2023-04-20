package com.worldplugins.caixas.config;

import com.worldplugins.lib.config.cache.InjectedConfigCache;
import com.worldplugins.lib.config.cache.annotation.ConfigSpec;
import com.worldplugins.lib.extension.CollectionExtensions;
import com.worldplugins.lib.extension.GenericExtensions;
import com.worldplugins.lib.extension.bukkit.ConfigurationExtensions;
import lombok.NonNull;
import lombok.experimental.ExtensionMethod;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ExtensionMethod(value = {
    ConfigurationExtensions.class,
    GenericExtensions.class,
    CollectionExtensions.class
}, suppressBaseMethods = false)

public class RewardsDataConfig implements InjectedConfigCache<Map<String, List<String>>> {
    @ConfigSpec(path = "recompensas")
    public @NonNull Map<String, List<String>> transform(@NonNull FileConfiguration config) {
        return config.getConfigurationSection("Data")
            .mapWithKeys((key, section) -> {
                final List<String> pages = ((Stream<String>) section.getKeys(false).stream())
                    .map(section::getString)
                    .collect(Collectors.toList());
                return key.to(pages);
            })
            .toMap();
    }
}
