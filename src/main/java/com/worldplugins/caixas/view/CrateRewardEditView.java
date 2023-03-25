package com.worldplugins.caixas.view;

import com.worldplugins.caixas.conversation.RewardChanceConversation;
import com.worldplugins.caixas.extension.ViewExtensions;
import com.worldplugins.caixas.rewards.ChanceReward;
import com.worldplugins.lib.api.storage.item.configuration.shelving.ShelvingConfigurationItemStorage;
import com.worldplugins.lib.api.storage.item.configuration.shelving.ShelvingItemKey;
import com.worldplugins.lib.common.Factory;
import com.worldplugins.lib.extension.NumberExtensions;
import com.worldplugins.lib.extension.bukkit.ItemExtensions;
import com.worldplugins.lib.util.data.ItemBuilder;
import com.worldplugins.lib.view.ContextView;
import com.worldplugins.lib.view.FullView;
import com.worldplugins.lib.view.ViewContext;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.util.Collections;

@ExtensionMethod({
    ViewExtensions.class,
    ItemExtensions.class,
    NumberExtensions.class
})

@RequiredArgsConstructor
public class CrateRewardEditView extends ContextView<CrateRewardEditView.Context> implements FullView {
    private final @NonNull ShelvingConfigurationItemStorage<ChanceReward> itemStorage;
    private final @NonNull Factory<ConversationFactory> conversationFactory;

    @RequiredArgsConstructor
    @Getter
    public static class Context implements ViewContext {
        private final @NonNull String id;
        private final int page;
        private final int slot;
        private final ChanceReward currentReward;

        public @NonNull Context changeReward(ChanceReward newReward) {
            return new Context(id, page, slot, newReward);
        }
    }

    @Override
    public void openView(@NonNull Player player, Context context) {
        saveCurrentItem(context);
        final String title = "Caixa '" + context.id + " P. " + (context.page + 1) + " S. " + context.slot;
        final Inventory inventory = Bukkit.createInventory(this, 36, title);
        final ChanceReward reward = itemStorage
            .getItem(new ShelvingItemKey(context.id, context.page, context.slot))
            .orElse(new ChanceReward(
                ItemBuilder.builder().id(166).name("&eEscolha um").build().create().inPlaceColorMeta(),
                0.0)
            );
        final String chanceFormat = ((Double) reward.getChance()).plainFormat() + "%";
        inventory.setItem(12, reward.getBukkitItem().clone());
        inventory.setItem(14, ItemBuilder.builder()
            .id(265)
            .name("&eChance &7- &f" + chanceFormat)
            .lore(Collections.singletonList("&7Clique para modificar"))
            .build().create()
            .inPlaceColorMeta()
        );
        inventory.setItem(27, ItemBuilder.builder().id(262).name("&eVoltar").build().create().inPlaceColorMeta());
        player.openInventory(inventory);
    }

    @Override
    public void onClick(@NonNull InventoryClickEvent event) {
        event.setCancelled(true);
        final Player player = (Player) event.getWhoClicked();

        if (event.getClickedInventory().getType() == InventoryType.CHEST) {
            final Context context = getContext(player);

            if (event.getSlot() == 12) {
                if (context.currentReward == null)
                    return;

                open(player, context.changeReward(null));
                return;
            }

            if (event.getSlot() == 14) {
                player.closeInventory();
                conversationFactory.create()
                    .withFirstPrompt(new RewardChanceConversation(context))
                    .withTimeout(15)
                    .withLocalEcho(false)
                    .buildConversation(player)
                    .begin();
                return;
            }

            if (event.getSlot() == 27) {
                player.openView(CrateRewardsPageView.class, new CrateRewardsPageView.Context(
                    context.page, context.id
                ));
            }
        }
    }

    @Override
    public void onBottomClick(@NonNull InventoryClickEvent event) {
        event.setCancelled(true);
        final Player player = (Player) event.getWhoClicked();

        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR)
            return;

        final Context context = getContext(player);
        player.openView(CrateRewardEditView.class, context.changeReward(new ChanceReward(
            event.getCurrentItem().clone(),
            context.currentReward == null ? 0.0 : context.currentReward.getChance()
        )));
    }

    private void saveCurrentItem(@NonNull Context context) {
        itemStorage.saveItem(new ShelvingItemKey(context.id, context.page, context.slot), context.currentReward);
    }
}
