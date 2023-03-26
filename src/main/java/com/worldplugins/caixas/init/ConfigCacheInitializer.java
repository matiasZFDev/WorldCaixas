package com.worldplugins.caixas.init;

import com.worldplugins.caixas.config.LocationsDataConfig;
import com.worldplugins.caixas.config.MainConfig;
import com.worldplugins.caixas.config.RewardsDataConfig;
import com.worldplugins.lib.common.Initializer;
import com.worldplugins.lib.config.cache.impl.EffectsConfig;
import com.worldplugins.lib.config.cache.impl.MessagesConfig;
import com.worldplugins.lib.config.cache.impl.SoundsConfig;
import com.worldplugins.lib.manager.config.ConfigCacheManager;
import com.worldplugins.lib.manager.config.ConfigCacheManagerImpl;
import com.worldplugins.lib.manager.config.ConfigManager;
import com.worldplugins.lib.registry.ConfigCacheRegistry;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.Plugin;

@RequiredArgsConstructor
public class ConfigCacheInitializer implements Initializer<ConfigCacheManager> {
    private final @NonNull Plugin plugin;
    private final @NonNull ConfigManager configManager;

    @Override
    public ConfigCacheManager init() {
        final ConfigCacheManager cacheManager = new ConfigCacheManagerImpl();
        final ConfigCacheRegistry registry = new ConfigCacheRegistry(cacheManager, configManager);
        registry.register(
            MessagesConfig.class,
            SoundsConfig.class,
            EffectsConfig.class,
            MainConfig.class,
            RewardsDataConfig.class,
            LocationsDataConfig.class
        );
        cacheManager.update();
        return cacheManager;
    }
}
