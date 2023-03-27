package com.worldplugins.caixas;

import com.worldplugins.caixas.rewards.ChanceReward;
import com.worldplugins.lib.api.storage.item.configuration.shelving.ShelvingConfigurationItemStorage;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Optional;

@RequiredArgsConstructor
public class CrateRewardHelper {
    private final @NonNull ShelvingConfigurationItemStorage<ChanceReward> itemStorage;

    public @NonNull Optional<ItemStack> openCrate(@NonNull String crateId) {
        return Arrays.stream(itemStorage.getItems(crateId))
            .filter(reward ->
                reward != null && (int) (Math.floor(Math.random() * (100.0 / reward.getChance()))) == 0
            )
            .findFirst()
            .map(ChanceReward::getBukkitItem);
    }
}
