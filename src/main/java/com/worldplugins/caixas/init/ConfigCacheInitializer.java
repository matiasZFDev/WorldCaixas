package com.worldplugins.caixas.init;

import com.worldplugins.caixas.config.MainConfig;
import com.worldplugins.caixas.config.RewardsDataConfig;
import com.worldplugins.lib.WorldLib;
import com.worldplugins.lib.common.ConfigCache;
import com.worldplugins.lib.common.Initializer;
import com.worldplugins.lib.common.Logger;
import com.worldplugins.lib.config.bukkit.ConfigContainer;
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
            RewardsDataConfig.class
        );
        registry.register(mainConfig());
        cacheManager.update();
        return cacheManager;
    }

    private @NonNull ConfigCache<?> mainConfig() {
        final String config = "config";
        configManager.load(config);
        final ConfigContainer configContainer = configManager.getContainer(config);
        final Logger logger = WorldLib.DEFAULT_LOGGERS.CONFIG_ERROR.apply(new WorldLib.DEFAULT_LOGGERS.PluginLogData(
            plugin.getName(), configContainer.path()
        ));
        return new MainConfig(logger, configContainer, plugin);
    }
}
