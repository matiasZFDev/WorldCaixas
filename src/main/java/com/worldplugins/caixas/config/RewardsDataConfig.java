package com.worldplugins.caixas.config;

import com.worldplugins.caixas.config.data.storage.ChanceRewardsStorage;
import com.worldplugins.caixas.config.data.storage.ChanceReward;
import com.worldplugins.lib.util.ConfigSections;
import com.worldplugins.lib.util.storage.item.ItemPage;
import com.worldplugins.lib.util.storage.item.ItemStorageSection;
import com.worldplugins.lib.util.storage.item.StorageData;
import com.worldplugins.lib.util.storage.item.impl.ListedItemStorageSection;
import com.worldplugins.lib.util.storage.item.impl.SimpleItemPage;
import me.post.lib.config.model.ConfigModel;
import me.post.lib.config.wrapper.ConfigWrapper;
import me.post.lib.util.BukkitSerializer;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class RewardsDataConfig implements ConfigModel<StorageData<ChanceReward>> {
    private @UnknownNullability StorageData<ChanceReward> data;
    private final @NotNull ConfigWrapper configWrapper;

    public RewardsDataConfig(@NotNull ConfigWrapper configWrapper) {
        this.configWrapper = configWrapper;
    }

    @Override
    public void update() {
        final FileConfiguration config = configWrapper.unwrap();
        final Collection<ItemStorageSection<ChanceReward>> sections = ConfigSections.mapWithKeys(
            config,
            (key, section) -> {
                final List<ItemPage<ChanceReward>> pages = section.getKeys(false).stream()
                    .map(pageKey -> new SimpleItemPage<>(
                            (ChanceReward[]) BukkitSerializer.deserialize(section.getString(pageKey))
                        )
                    )
                    .collect(Collectors.toList());
                return new ListedItemStorageSection<>(key, pages);
            });
        data = new ChanceRewardsStorage(sections);
    }

    @Override
    public @NotNull StorageData<ChanceReward> data() {
        return data;
    }

    @Override
    public @NotNull ConfigWrapper wrapper() {
        return configWrapper;
    }
}
