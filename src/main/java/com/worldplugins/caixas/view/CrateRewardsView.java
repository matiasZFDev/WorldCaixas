package com.worldplugins.caixas.view;

import com.worldplugins.caixas.config.menu.CrateRewardsMenuContainer;
import com.worldplugins.caixas.controller.RewardsController;
import com.worldplugins.caixas.rewards.ChanceReward;
import com.worldplugins.lib.common.Pair;
import com.worldplugins.lib.config.cache.menu.ItemProcessResult;
import com.worldplugins.lib.config.cache.menu.MenuData;
import com.worldplugins.lib.config.cache.menu.MenuItem;
import com.worldplugins.lib.extension.*;
import com.worldplugins.lib.extension.bukkit.ItemExtensions;
import com.worldplugins.lib.util.MenuItemsUtils;
import com.worldplugins.lib.view.MenuDataView;
import com.worldplugins.lib.view.ViewContext;
import com.worldplugins.lib.view.annotation.ViewSpec;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Collections;

@ExtensionMethod({
    ReplaceExtensions.class,
    GenericExtensions.class,
    ItemExtensions.class,
    CollectionExtensions.class,
    NumberExtensions.class
})

@RequiredArgsConstructor
@ViewSpec(menuContainer = CrateRewardsMenuContainer.class)
public class CrateRewardsView extends MenuDataView<CrateRewardsView.Context> {
    @RequiredArgsConstructor
    public static class Context implements ViewContext {
        private final String crateId;
        private final int page;
        private final int totalPages;
        private final Collection<Pair<Integer, ChanceReward>> pageRewards;

        @Override
        public ViewContext viewDidOpen() {
            return new Context(crateId, page, totalPages, null);
        }
    }

    private final @NonNull RewardsController rewardsController;

    @Override
    public @NonNull ItemProcessResult processItems(
        @NonNull Player player,
        @NonNull Context context,
        @NonNull MenuData menuData
    ) {
        return MenuItemsUtils.newSession(menuData.getItems(), session -> {
            if (context.page == 0)
                session.remove("Pagina-anterior");

            if (context.page + 1 == context.totalPages)
                session.remove("Pagina-seguinte");

            session.addDynamics(() -> context.pageRewards
                .mapIndexed((index, pair) -> {
                    final int position = (index * context.page) + 1;
                    final ItemStack item = pair.second().getBukkitItem();
                    final ItemStack rewardItemModel = menuData.getData("Recompensa-iten");
                    ItemStack rewardItem = item.clone()
                        .name(rewardItemModel.getItemMeta().getDisplayName())
                        .lore(rewardItemModel.getItemMeta().getLore())
                        .loreFormat(
                            "@posicao".to(String.valueOf(position)),
                            "@chance".to(((Double) pair.second().getChance()).plainFormat())
                        );

                    if (!item.hasItemMeta()) {
                        rewardItem.name(null);
                        rewardItem = rewardItem.mutableLoreListFormat("@@lore", Collections.emptyList());
                    } else {
                        if (item.getItemMeta().hasDisplayName())
                            rewardItem.mutableNameFormat("@name".to(item.getItemMeta().getDisplayName()));
                        else
                            rewardItem.name(null);

                        rewardItem.mutableLoreListFormat("@@lore", item.getItemMeta().hasLore()
                            ? item.getItemMeta().getLore()
                            : Collections.emptyList()
                        );
                    }

                    return new MenuItem("Recompensa", pair.first(), rewardItem.inPlaceColorMeta(), null);
                })
            );
        }).build();
    }

    @Override
    public @NonNull String getTitle(@NonNull String title, @NonNull Context data) {
        return title.formatReplace(
            "@caixa".to(data.crateId),
            "@atual".to(String.valueOf(data.page + 1)),
            "@totais".to(String.valueOf(data.totalPages))
        );
    }

    @Override
    public void onClick(@NonNull Player player, @NonNull MenuItem item, @NonNull InventoryClickEvent event) {
        if (item.getId().equals("Pagina-seguinte")) {
            final Context context = getContext(player);
            rewardsController.updateView(player, context.crateId, context.page + 1);
            return;
        }

        if (item.getId().equals("Pagina-anterior")) {
            final Context context = getContext(player);
            rewardsController.updateView(player, context.crateId, context.page - 1);
        }
    }
}
