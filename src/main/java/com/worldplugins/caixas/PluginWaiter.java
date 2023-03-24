package com.worldplugins.caixas;

import com.worldplugins.caixas.command.GiveKey;
import com.worldplugins.caixas.command.GiveKeyAll;
import com.worldplugins.caixas.command.GiveLocator;
import com.worldplugins.caixas.config.MainConfig;
import com.worldplugins.lib.config.cache.impl.EffectsConfig;
import com.worldplugins.lib.config.cache.impl.MessagesConfig;
import com.worldplugins.lib.config.cache.impl.SoundsConfig;
import com.worldplugins.lib.registry.CommandRegistry;
import com.worldplugins.lib.registry.ViewRegistry;
import com.worldplugins.caixas.init.ConfigCacheInitializer;
import com.worldplugins.lib.manager.config.ConfigCacheManager;
import com.worldplugins.lib.manager.config.ConfigManager;
import com.worldplugins.lib.manager.config.YamlConfigManager;
import com.worldplugins.lib.manager.view.MenuContainerManager;
import com.worldplugins.lib.manager.view.MenuContainerManagerImpl;
import com.worldplugins.lib.manager.view.ViewManager;
import com.worldplugins.lib.manager.view.ViewManagerImpl;
import com.worldplugins.lib.util.SchedulerBuilder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

@RequiredArgsConstructor
public class PluginWaiter {
    private final @NonNull JavaPlugin plugin;
    private final @NonNull SchedulerBuilder scheduler;
    private final @NonNull ConfigManager configManager;
    private final @NonNull ConfigCacheManager configCacheManager;
    private final @NonNull MenuContainerManager menuContainerManager;
    private final @NonNull ViewManager viewManager;

    public PluginWaiter(@NonNull JavaPlugin plugin) {
        this.plugin = plugin;
        scheduler = new SchedulerBuilder(plugin);
        configManager = new YamlConfigManager(plugin);
        configCacheManager = new ConfigCacheInitializer(plugin, configManager).init();
        menuContainerManager = new MenuContainerManagerImpl();
        viewManager = new ViewManagerImpl();
    }

    /**
     * @return A runnable executed when disabling
     * */
    public @NonNull Runnable execute() {
        setGlobalResponseAccess();
        registerListeners();
        registerCommands();
        scheduleTasks();
        return () -> {};
    }

    private void setGlobalResponseAccess() {
        GlobalAccess.setMessages(configCacheManager.get(MessagesConfig.class));
        GlobalAccess.setSounds(configCacheManager.get(SoundsConfig.class));
        GlobalAccess.setEffects(configCacheManager.get(EffectsConfig.class));
        GlobalAccess.setViewManager(viewManager);
    }

    private void regListeners(@NonNull Listener... listeners) {
        final PluginManager pluginManager = plugin.getServer().getPluginManager();
        for (final Listener listener : listeners) {
            pluginManager.registerEvents(listener, plugin);
        }
    }

    private void registerListeners() {

    }

    private void registerCommands() {
        final CommandRegistry registry = new CommandRegistry(plugin);
        final MainConfig mainConfig = configCacheManager.get(MainConfig.class);
        final KeyFactory keyFactory = new KeyFactory(mainConfig);

        registry.command(
            new GiveLocator(mainConfig),
            new GiveKey(keyFactory, mainConfig),
            new GiveKeyAll(keyFactory, mainConfig)
        );
        registry.registerAll();
    }

    private void registerViews() {
        final ViewRegistry registry = new ViewRegistry(viewManager, menuContainerManager, configManager);
        registry.register();
    }

    private void scheduleTasks() {

    }
}
