package com.worldplugins.caixas.config;

import com.worldplugins.lib.common.Logger;
import com.worldplugins.lib.config.bukkit.ConfigContainer;
import com.worldplugins.lib.config.cache.StateConfig;
import com.worldplugins.lib.config.cache.annotation.Config;
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

@Config(path = "recompensas")
public class RewardsDataConfig extends StateConfig<Map<String, List<String>>> {

    public RewardsDataConfig(Logger logger, @NonNull ConfigContainer configContainer) {
        super(logger, configContainer);
    }

    @Override
    public @NonNull Map<String, List<String>> fetch(@NonNull FileConfiguration config) {
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
