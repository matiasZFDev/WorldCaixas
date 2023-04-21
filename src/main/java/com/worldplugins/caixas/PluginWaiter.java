package com.worldplugins.caixas;

import com.worldplugins.caixas.command.*;
import com.worldplugins.caixas.config.LocationsDataConfig;
import com.worldplugins.caixas.config.MainConfig;
import com.worldplugins.caixas.config.RewardsDataConfig;
import com.worldplugins.caixas.controller.RewardsController;
import com.worldplugins.caixas.factory.KeyFactory;
import com.worldplugins.caixas.listener.*;
import com.worldplugins.caixas.manager.CrateManager;
import com.worldplugins.caixas.rewards.ChanceReward;
import com.worldplugins.caixas.view.CrateRewardEditView;
import com.worldplugins.caixas.view.CrateRewardsPageView;
import com.worldplugins.caixas.view.CrateRewardsView;
import com.worldplugins.lib.api.storage.item.configuration.shelving.ShelvingConfigurationItemStorage;
import com.worldplugins.lib.common.Factory;
import com.worldplugins.lib.config.cache.ConfigCache;
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
import com.worldplugins.lib.util.ConversationProvider;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

@RequiredArgsConstructor
public class PluginWaiter {
    private final @NonNull JavaPlugin plugin;
    private final @NonNull ConfigManager configManager;
    private final @NonNull ConfigCacheManager configCacheManager;
    private final @NonNull MenuContainerManager menuContainerManager;
    private final @NonNull ViewManager viewManager;
    private final @NonNull CrateManager crateManager;
    private final ShelvingConfigurationItemStorage<ChanceReward> itemStorage;
    private final @NonNull RewardsController rewardsController;

    public PluginWaiter(@NonNull JavaPlugin plugin) {
        this.plugin = plugin;
        configManager = new YamlConfigManager(plugin);
        configCacheManager = new ConfigCacheInitializer(configManager).init();
        menuContainerManager = new MenuContainerManagerImpl();
        viewManager = new ViewManagerImpl();
        crateManager = new CrateManager(plugin, config(MainConfig.class), config(LocationsDataConfig.class));
        itemStorage = new ShelvingConfigurationItemStorage<>(
            36, config(RewardsDataConfig.class), ChanceReward[]::new
        );
        rewardsController = new RewardsController(config(MainConfig.class), itemStorage);
    }

    /**
     * @return A runnable executed when disabling
     * */
    public @NonNull Runnable execute() {
        prepareGlobalAccess();
        loadPendings();
        registerListeners();
        registerCommands();
        registerViews();
        return crateManager::disableAll;
    }

    private void prepareGlobalAccess() {
        GlobalAccess.setMessages(config(MessagesConfig.class));
        GlobalAccess.setSounds(config(SoundsConfig.class));
        GlobalAccess.setEffects(config(EffectsConfig.class));
        GlobalAccess.setViewManager(viewManager);
    }

    private void loadPendings() {
        crateManager.update();
    }

    private void regListeners(@NonNull Listener... listeners) {
        final PluginManager pluginManager = plugin.getServer().getPluginManager();
        for (final Listener listener : listeners) {
            pluginManager.registerEvents(listener, plugin);
        }
    }

    private void registerListeners() {
        final CrateRewardHelper rewardHelper = new CrateRewardHelper(itemStorage);
        regListeners(
            new CrateLocateListener(config(MainConfig.class), crateManager),
            new CrateUnlocateListener(crateManager),
            new CrateAnimationListener(),
            new CrateRewardsOverviewListener(crateManager, rewardsController),
            new CrateOpenListener(crateManager, rewardHelper)
        );
    }

    private <T> T config(@NonNull Class<? extends ConfigCache<?>> key) {
        return configCacheManager.get(key);
    }

    private void registerCommands() {
        final CommandRegistry registry = new CommandRegistry(plugin);
        final KeyFactory keyFactory = new KeyFactory(config(MainConfig.class));

        registry.command(
            new Help(),
            new Reload(configManager, configCacheManager, menuContainerManager, crateManager),
            new GiveLocator(config(MainConfig.class)),
            new GiveKey(keyFactory, config(MainConfig.class)),
            new GiveKeyAll(keyFactory, config(MainConfig.class)),
            new Rewards(config(MainConfig.class))
        );
        registry.autoTabCompleter("caixas");
        registry.registerAll();
    }

    private void registerViews() {
        final ViewRegistry registry = new ViewRegistry(viewManager, menuContainerManager, configManager);
        final Factory<ConversationFactory> conversationProvider = new ConversationProvider(plugin);

        registry.register(
            new CrateRewardsPageView(itemStorage),
            new CrateRewardEditView(itemStorage, conversationProvider),
            new CrateRewardsView(rewardsController)
        );
    }
}
