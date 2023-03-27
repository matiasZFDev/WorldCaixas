package com.worldplugins.caixas.controller;

import com.worldplugins.caixas.config.MainConfig;
import com.worldplugins.caixas.extension.ViewExtensions;
import com.worldplugins.caixas.rewards.ChanceReward;
import com.worldplugins.caixas.view.CrateRewardsView;
import com.worldplugins.lib.api.storage.item.configuration.shelving.ShelvingConfigurationItemStorage;
import com.worldplugins.lib.common.Pair;
import com.worldplugins.lib.extension.CollectionExtensions;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

@ExtensionMethod({
    ViewExtensions.class,
    CollectionExtensions.class
})

@RequiredArgsConstructor
public class RewardsController {
    @RequiredArgsConstructor
    private static class Data {
        private final @NonNull Collection<Pair<Integer, ChanceReward>> pageItems;
        private final int itemCount;
    }
    private final @NonNull MainConfig mainConfig;
    private final @NonNull ShelvingConfigurationItemStorage<ChanceReward> itemStorage;

    public void updateView(@NonNull Player player, @NonNull String crateId, int page) {
        final Data data = getPageItems(crateId, page);

        if (page > 0 && data.pageItems.isEmpty()) {
            updateView(player, crateId, 0);
            return;
        }

        final int totalPages = data.itemCount == 0 && data.itemCount == mainConfig.get().getRewardsSlots().size()
            ? 1
            : data.itemCount / mainConfig.get().getRewardsSlots().size() + 1;
        player.openView(CrateRewardsView.class, new CrateRewardsView.Context(
            crateId, page, totalPages, data.pageItems
        ));
    }

    private @NonNull Data getPageItems(@NonNull String crateId, int page) {
        final Collection<ChanceReward> nonNullItems = getNonNullItems(crateId);
        final List<ChanceReward> pageItems = nonNullItems.stream()
            .skip((long) page * mainConfig.get().getRewardsSlots().size())
            .limit(mainConfig.get().getRewardsSlots().size())
            .collect(Collectors.toList());
        return new Data(mainConfig.get().getRewardsSlots().zip(pageItems), nonNullItems.size());
    }

    private @NonNull Collection<ChanceReward> getNonNullItems(@NonNull String crateId) {
        return Arrays.stream(itemStorage.getItems(crateId))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }
}
