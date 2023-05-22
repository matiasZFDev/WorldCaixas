package com.worldplugins.caixas.view;

import com.worldplugins.caixas.NBTKeys;
import com.worldplugins.caixas.config.data.storage.ChanceReward;
import com.worldplugins.lib.util.storage.item.PersistentItemStorage;
import com.worldplugins.lib.util.storage.item.StorageKey;
import me.post.deps.nbt_api.nbtapi.NBTCompound;
import me.post.lib.util.*;
import me.post.lib.view.View;
import me.post.lib.view.Views;
import me.post.lib.view.action.ViewClick;
import me.post.lib.view.action.ViewClose;
import me.post.lib.view.helper.ClickHandler;
import me.post.lib.view.helper.ViewContext;
import me.post.lib.view.helper.impl.MapViewContext;
import me.post.lib.view.helper.page.PageContextBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Objects.requireNonNull;

public class CrateRewardsPageView implements View {
    public static class Context {
        private final @NotNull String sectionId;
        private final int page;

        public Context(@NotNull String sectionId, int page) {
            this.sectionId = sectionId;
            this.page = page;
        }
    }

    private final @NotNull ViewContext viewContext;
    private final @NotNull PersistentItemStorage<ChanceReward> itemStorage;

    public CrateRewardsPageView(@NotNull PersistentItemStorage<ChanceReward> itemStorage) {
        this.viewContext = new MapViewContext();
        this.itemStorage = itemStorage;
    }

    @Override
    public void open(@NotNull Player player, @Nullable Object data) {
        final Context context = (Context) requireNonNull(data);
        final AtomicInteger rewardSlot = new AtomicInteger(0);

        PageContextBuilder.of(
                page -> Views.get().open(player, getClass(), new Context(context.sectionId, page)),
                context.page,
                6,
                pageInfo ->"Caixa '" + context.sectionId + "' P." + pageInfo.page()
            )
            .withLayout(
                "         ",
                " OOOOOOO ",
                " OOOOOOO ",
                " OOOOOOO ",
                " OOOOOOO ",
                "         "
            )
            .fill(
                Arrays.asList(itemStorage.getPageItems(context.sectionId, context.page)),
                reward -> this.getBukkitItem(rewardSlot.getAndIncrement(), reward),
                click -> {
                    final int clickedRewardSlot = NBTs.getTagValue(
                        requireNonNull(click.clickedItem()), NBTKeys.REWARD_SLOT, NBTCompound::getInteger
                    );
                    final StorageKey itemKey = new StorageKey(context.sectionId, context.page, clickedRewardSlot);

                    if (click.clickType() == ClickType.SHIFT_RIGHT) {
                        itemStorage.saveItem(itemKey, null);
                        open(player, context);
                        return;
                    }

                    if (click.clickType() == ClickType.LEFT) {
                        final ChanceReward reward = itemStorage.getItem(itemKey);

                        Views.get().open(player, CrateRewardEditView.class, new CrateRewardEditView.Context(
                            context.sectionId, context.page, clickedRewardSlot, reward
                        ));
                    }
                }
            )
            .build(this.viewContext, player, context);
    }

    private @NotNull ItemStack getBukkitItem(int slot, @Nullable ChanceReward reward) {
        final ItemStack item;

        if (reward != null) {
            final String chanceFormat = Numbers.plainFormat(reward.chance()) + "%";
            item = Items.modifyMeta(reward.bukkitItem().clone(), meta ->
                meta.setLore(Colors.color(Arrays.asList(
                    "&8Uma das recompensas...",
                    "",
                    " &fChance: &e" + chanceFormat,
                    "",
                    "&fB. esquerdo: &7Editar recompensa",
                    "&fB. direito + SHIFT: &7Remover recompensa")
                ))
            );
        } else {
            item = ItemBuilder.of()
                .material(Material.BARRIER)
                .name(Colors.color("&cSlot vazio"))
                .lore(Colors.color(Arrays.asList("&7Clique para modificar", "&7esse slot.")))
                .build();
        }

        return NBTs.modifyTags(item, nbtItem ->
            nbtItem.setInteger(NBTKeys.REWARD_SLOT, slot)
        );
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
