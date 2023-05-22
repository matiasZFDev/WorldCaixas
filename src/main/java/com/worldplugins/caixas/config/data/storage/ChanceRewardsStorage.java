package com.worldplugins.caixas.config.data.storage;

import com.worldplugins.lib.util.storage.item.ItemStorageSection;
import com.worldplugins.lib.util.storage.item.StorageData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ChanceRewardsStorage implements StorageData<ChanceReward> {
    private final @NotNull Map<String, ItemStorageSection<ChanceReward>> sections;

    public ChanceRewardsStorage(@NotNull Collection<ItemStorageSection<ChanceReward>> sections) {
        this.sections = sections.stream().collect(Collectors.toMap(ItemStorageSection::id, Function.identity()));
    }

    @Override
    public @Nullable ItemStorageSection<ChanceReward> sectionById(@NotNull String id) {
        return sections.get(id);
    }

    @Override
    public @NotNull Collection<ItemStorageSection<ChanceReward>> allSections() {
        return sections.values();
    }
}
