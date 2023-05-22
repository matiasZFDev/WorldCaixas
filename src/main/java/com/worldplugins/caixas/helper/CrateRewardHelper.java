package com.worldplugins.caixas.helper;

import com.worldplugins.caixas.config.data.storage.ChanceReward;
import com.worldplugins.lib.util.storage.item.PersistentItemStorage;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CrateRewardHelper {
    private final @NotNull PersistentItemStorage<ChanceReward> itemStorage;

    public CrateRewardHelper(@NotNull PersistentItemStorage<ChanceReward> itemStorage) {
        this.itemStorage = itemStorage;
    }

    public ItemStack openCrate(@NotNull String crateId) {
        return itemStorage.getAllItems(crateId).stream()
            .filter(reward ->
                reward != null && (int) (Math.floor(Math.random() * (100.0 / reward.chance()))) == 0
            )
            .findFirst()
            .map(ChanceReward::bukkitItem   )
            .orElse(null);
    }
}
