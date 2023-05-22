package com.worldplugins.caixas;

import com.worldplugins.caixas.command.*;
import com.worldplugins.caixas.config.LocationsDataConfig;
import com.worldplugins.caixas.config.MainConfig;
import com.worldplugins.caixas.config.RewardsDataConfig;
import com.worldplugins.caixas.config.data.LocationsData;
import com.worldplugins.caixas.config.data.MainData;
import com.worldplugins.caixas.config.menu.CrateRewardsMenuModel;
import com.worldplugins.caixas.factory.KeyFactory;
import com.worldplugins.caixas.helper.CrateRewardHelper;
import com.worldplugins.caixas.listener.*;
import com.worldplugins.caixas.manager.CrateManager;
import com.worldplugins.caixas.config.data.storage.ChanceReward;
import com.worldplugins.caixas.view.CrateRewardEditView;
import com.worldplugins.caixas.view.CrateRewardsPageView;
import com.worldplugins.caixas.view.CrateRewardsView;
import com.worldplugins.lib.config.Updatables;
import com.worldplugins.lib.util.storage.item.PersistentItemStorage;
import com.worldplugins.lib.util.storage.item.StorageData;
import com.worldplugins.lib.util.storage.item.impl.ConfigPersistentItemStorage;
import me.post.lib.command.process.CommandRegistry;
import me.post.lib.config.model.ConfigModel;
import me.post.lib.config.wrapper.ConfigManager;
import me.post.lib.config.wrapper.YamlConfigManager;
import me.post.lib.util.ConversationProvider;
import me.post.lib.util.Scheduler;
import me.post.lib.view.Views;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class PluginExecutor {
    private final @NotNull JavaPlugin plugin;
    private final @NotNull ConfigManager configManager;
    private final @NotNull Scheduler scheduler;
    private final @NotNull CrateManager crateManager;
    private final @NotNull PersistentItemStorage<ChanceReward> itemStorage;
    private final @NotNull Updatables updatables;
    
    private final @NotNull ConfigModel<MainData> mainConfig;
    private final @NotNull ConfigModel<LocationsData> locationsConfig;
    private final @NotNull ConfigModel<StorageData<ChanceReward>> rewardsConfig;

    public PluginExecutor(@NotNull JavaPlugin plugin) {
        this.plugin = plugin;
        scheduler = new Scheduler(plugin);
        configManager = new YamlConfigManager(plugin);
        loadConfiguration();

        updatables = new Updatables();
        mainConfig = updatables.include(new MainConfig(configManager.getWrapper("config")));
        locationsConfig = updatables.include(new LocationsDataConfig(configManager.getWrapper("localizacoes")));
        rewardsConfig = updatables.include(new RewardsDataConfig(configManager.getWrapper("recompensas")));

        crateManager = updatables.include(new CrateManager(scheduler, mainConfig, locationsConfig));
        itemStorage = new ConfigPersistentItemStorage<>(36, rewardsConfig, ChanceReward[]::new);
    }

    private void loadConfiguration() {
        Arrays.asList(
            "resposta/mensagens", "resposta/sons", "resposta/efeitos", "config", "localizacoes", "recompensas",
            "menu/recompensas"
        ).forEach(configManager::load);
    }

    /**
     * @return A runnable executed when disabling
     * */
    public @NotNull Runnable execute() {
        setupGlobalResponse();
        registerListeners();
        registerCommands();
        registerViews();
        updatables.update();
        return crateManager::disableAll;
    }

    private void setupGlobalResponse() {
        Response.setup(updatables, configManager);
    }

    private void regListeners(@NotNull Listener... listeners) {
        final PluginManager pluginManager = plugin.getServer().getPluginManager();
        for (final Listener listener : listeners) {
            pluginManager.registerEvents(listener, plugin);
        }
    }

    private void registerListeners() {
        final CrateRewardHelper rewardHelper = new CrateRewardHelper(itemStorage);
        regListeners(
            new CrateLocateListener(mainConfig, crateManager),
            new CrateUnlocateListener(crateManager),
            new CrateAnimationListener(),
            new CrateRewardsOverviewListener(crateManager),
            new CrateOpenListener(crateManager, rewardHelper)
        );
    }

    private void registerCommands() {
        final CommandRegistry registry = CommandRegistry.on(plugin);
        final KeyFactory keyFactory = new KeyFactory(mainConfig);

        registry.addModules(
            new HelpCommand(),
            new ReloadCommand(configManager, updatables),
            new GiveLocatorCommand(mainConfig),
            new GiveKeyCommand(keyFactory, mainConfig),
            new GiveKeyAllCommand(keyFactory, mainConfig),
            new RewardsCommand(mainConfig)
        );
        registry.registerAll();
    }

    private void registerViews() {
        final ConversationProvider conversationProvider = new ConversationProvider(plugin);

        Views.get().register(
            new CrateRewardsPageView(itemStorage),
            new CrateRewardEditView(itemStorage, conversationProvider),
            new CrateRewardsView(
                updatables.include(new CrateRewardsMenuModel(configManager.getWrapper("menu/recompensas"))),
                itemStorage
            )
        );
    }
}
