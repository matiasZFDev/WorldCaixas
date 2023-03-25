package com.worldplugins.caixas.view;

import com.worldplugins.caixas.extension.ViewExtensions;
import com.worldplugins.caixas.rewards.ChanceReward;
import com.worldplugins.lib.api.storage.item.configuration.shelving.ShelvingConfigurationItemStorage;
import com.worldplugins.lib.api.storage.item.configuration.shelving.ShelvingItemKey;
import com.worldplugins.lib.api.storage.item.view.PageContext;
import com.worldplugins.lib.api.storage.item.view.PaginatedItemStorageView;
import com.worldplugins.lib.extension.NumberExtensions;
import com.worldplugins.lib.extension.bukkit.ColorExtensions;
import com.worldplugins.lib.extension.bukkit.ItemExtensions;
import com.worldplugins.lib.util.data.ItemBuilder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Optional;

@ExtensionMethod({
    ColorExtensions.class,
    ItemExtensions.class,
    ViewExtensions.class,
    NumberExtensions.class
})

@RequiredArgsConstructor
public class CrateRewardsPageView extends PaginatedItemStorageView<ChanceReward, CrateRewardsPageView.Context> {
    private final @NonNull ShelvingConfigurationItemStorage<ChanceReward> itemStorage;

    public static class Context extends PageContext {
        @Getter
        private final @NonNull String id;

        public Context(int page, @NonNull String id) {
            super(page);
            this.id = id;
        }
    }

    @Override
    public @NonNull ChanceReward[] getItems(Context context) {
        return itemStorage.getPageItems(context.id, context.getPage());
    }

    @Override
    public @NonNull String getTitle(@NonNull Context context) {
        return "Caixa '" + context.id + "' P. " + (context.getPage() + 1);
    }

    @Override
    public int pageSize() {
        return 36;
    }

    @Override
    public @NonNull ItemStack getBukkitItem(ChanceReward reward) {
        if (reward != null && reward.getBukkitItem() != null) {
            final String chanceFormat = ((Double) reward.getChance()).plainFormat() + "%";
            return reward.getBukkitItem()
                .clone()
                .lore(Arrays.asList(
                    "&8Uma das recompensas...",
                    "",
                    " &fChance: &e" + chanceFormat,
                    "",
                    "&fB. esquerdo: &7Editar recompensa",
                    "&fB. direito + SHIFT: &7Remover recompensa"
                ).color());
        }

        return ItemBuilder.builder()
            .id(160)
            .data((short) 1)
            .name("&cSlot vazio".color())
            .lore(Arrays.asList("&7Clique para modificar", "&7esse slot.").color())
            .build()
            .create();
    }

    @NonNull
    @Override
    public Context buildContext(Context context, int newPage) {
        System.out.println(newPage);
        return new Context(newPage, context.id);
    }

    @Override
    public void onTopClick(@NonNull InventoryClickEvent event) {
        event.setCancelled(true);

        final Player player = (Player) event.getWhoClicked();
        final Context context = getContext(player);
        final int slot = event.getSlot();
        final ShelvingItemKey key = new ShelvingItemKey(context.id, context.getPage(), slot);

        if (event.getClick() == ClickType.SHIFT_RIGHT) {
            itemStorage.saveItem(key, null);
            open(player, context);
            return;
        }

        final Optional<ChanceReward> reward = itemStorage.getItem(key);
        player.openView(CrateRewardEditView.class, new CrateRewardEditView.Context(
            context.id, context.getPage(), slot, reward.orElse(null)
        ));
    }
}
