package com.worldplugins.caixas.view;

import com.worldplugins.caixas.config.data.storage.ChanceReward;
import com.worldplugins.lib.config.model.MenuModel;
import com.worldplugins.lib.util.ItemBuilding;
import com.worldplugins.lib.util.Strings;
import com.worldplugins.lib.util.storage.item.PersistentItemStorage;
import com.worldplugins.lib.view.PageConfigContextBuilder;
import me.post.lib.util.Items;
import me.post.lib.util.Numbers;
import me.post.lib.view.View;
import me.post.lib.view.Views;
import me.post.lib.view.action.ViewClick;
import me.post.lib.view.action.ViewClose;
import me.post.lib.view.context.ClickHandler;
import me.post.lib.view.context.ViewContext;
import me.post.lib.view.context.impl.MapViewContext;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Objects.requireNonNull;
import static me.post.lib.util.Pairs.to;

public class CrateRewardsView implements View {
    public static class Context {
        private final @NotNull String crateId;
        private final int page;

        public Context(@NotNull String crateId, int page) {
            this.crateId = crateId;
            this.page = page;
        }
    }

    private final @NotNull ViewContext viewContext;
    private final @NotNull MenuModel menuModel;
    private final @NotNull PersistentItemStorage<ChanceReward> itemStorage;

    public CrateRewardsView(@NotNull MenuModel menuModel, @NotNull PersistentItemStorage<ChanceReward> itemstorage) {
        this.viewContext = new MapViewContext();
        this.menuModel = menuModel;
        this.itemStorage = itemstorage;
    }

    @Override
    public void open(@NotNull Player player, @Nullable Object data) {
        final Context context = (Context) requireNonNull(data);
        final List<Integer> pageSlots = menuModel.data().getData("Slots");
        final AtomicInteger rewardIndex = new AtomicInteger(0);

        PageConfigContextBuilder.of(
                menuModel,
                page -> Views.get().open(player, CrateRewardsView.class, new Context(context.crateId, page)),
                context.page
            )
            .editTitle((pageInfo, title) -> Strings.replace(title,
                to("@caixa", context.crateId),
                to("@atual", String.valueOf(pageInfo.page() + 1)),
                to("@totais", String.valueOf(pageInfo.totalPages()))
            ))
            .nextPageButtonAs("Pagina-seguinte")
            .previousPageButtonAs("Pagina-anterior")
            .withSlots(pageSlots)
            .fill(
                itemStorage.getAllItems(context.crateId),
                reward -> {
                    final int position = (rewardIndex.getAndIncrement() * context.page) + 1;
                    final ItemStack item = reward.bukkitItem();
                    final ItemStack rewardItemModel = menuModel.data().getData("Recompensa-iten");
                    final ItemStack rewardItem = Items.modifyMeta(item.clone(), meta -> {
                        final List<String> lore = Strings.replace(
                            rewardItemModel.getItemMeta().getLore(),
                            to("@posicao", String.valueOf(position)),
                            to("@chance", Numbers.plainFormat(reward.chance()))
                        );
                        meta.setDisplayName(rewardItemModel.getItemMeta().getDisplayName());
                        meta.setLore(lore);
                    });

                    if (!item.hasItemMeta()) {
                        Items.modifyMeta(rewardItem, meta -> meta.setDisplayName(null));
                        ItemBuilding.loreListFormat(rewardItem, "@@lore", Collections.emptyList());
                    } else {
                        if (item.getItemMeta().hasDisplayName()) {
                            ItemBuilding.nameFormat(rewardItem, to("@name", item.getItemMeta().getDisplayName()));
                        } else {
                            Items.modifyMeta(rewardItem, meta -> meta.setDisplayName(null));
                        }

                        ItemBuilding.loreListFormat(rewardItem, "@@lore", item.getItemMeta().hasLore()
                            ? item.getItemMeta().getLore()
                            : Collections.emptyList()
                        );
                    }

                    return rewardItem;
                }
            )
            .build(viewContext, player, data);
    }

    @Override
    public void onClick(@NotNull ViewClick click) {
        ClickHandler.handleTopNonNull(viewContext, click);
    }

    @Override
    public void onClose(@NotNull ViewClose close) {
        viewContext.removeViewer(close.whoCloses().getUniqueId());
    }
}
