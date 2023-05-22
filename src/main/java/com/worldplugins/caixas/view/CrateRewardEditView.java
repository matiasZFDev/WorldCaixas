package com.worldplugins.caixas.view;

import com.worldplugins.caixas.conversation.RewardChanceConversation;
import com.worldplugins.caixas.config.data.storage.ChanceReward;
import com.worldplugins.lib.util.storage.item.PersistentItemStorage;
import com.worldplugins.lib.util.storage.item.StorageKey;
import me.post.lib.util.ConversationProvider;
import me.post.lib.util.ItemBuilder;
import me.post.lib.util.Numbers;
import me.post.lib.view.View;
import me.post.lib.view.Views;
import me.post.lib.view.action.ClickPosition;
import me.post.lib.view.action.ViewClick;
import me.post.lib.view.action.ViewClose;
import me.post.lib.view.helper.ClickHandler;
import me.post.lib.view.helper.ContextBuilder;
import me.post.lib.view.helper.ViewContext;
import me.post.lib.view.helper.impl.MapViewContext;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;

import static java.util.Objects.requireNonNull;
import static me.post.lib.util.Colors.color;

public class CrateRewardEditView implements View {
    public static class Context {
        private final @NotNull String id;
        private final int page;
        private final int slot;
        private final @Nullable ChanceReward currentReward;

        public Context(@NotNull String id, int page, int slot, @Nullable ChanceReward currentReward) {
            this.id = id;
            this.page = page;
            this.slot = slot;
            this.currentReward = currentReward;
        }

        public @NotNull Context changeReward(@Nullable ChanceReward newReward) {
            return new Context(id, page, slot, newReward);
        }

        public @Nullable ChanceReward currentReward() {
            return currentReward;
        }
    }

    private final @NotNull ViewContext viewContext;
    private final @NotNull PersistentItemStorage<ChanceReward> itemStorage;
    private final @NotNull ConversationProvider conversationFactory;

    public CrateRewardEditView(@NotNull PersistentItemStorage<ChanceReward> itemStorage, @NotNull ConversationProvider conversationFactory) {
        this.viewContext = new MapViewContext();
        this.itemStorage = itemStorage;
        this.conversationFactory = conversationFactory;
    }

    @Override
    public void open(@NotNull Player player, @Nullable Object data) {
        final Context context = (Context) requireNonNull(data);
        final ChanceReward reward = context.currentReward;
        final String chanceFormat = reward == null
            ? "0%"
            : Numbers.plainFormat(reward.chance()) + "%";

        ContextBuilder.of(4, "Caixa '" + context.id + "' P." + (context.page + 1) + " S." + context.slot)
            .item(
                12,
                reward != null
                    ? reward.bukkitItem().clone()
                    : ItemBuilder.of()
                        .material(Material.BARRIER)
                        .name(color("&eEscolha um iten."))
                        .build(),
                click -> {
                    if (context.currentReward == null) {
                        return;
                    }

                    open(player, context.changeReward(null));
                }
            )
            .item(
                14,
                ItemBuilder.of()
                    .material(Material.EMERALD)
                    .name(color("&eChance &7- &f" + chanceFormat))
                    .lore(Collections.singletonList(color("&7Clique para modificar")))
                    .build(),
                click -> {
                    if (context.currentReward == null) {
                        player.sendMessage(color("&cEscolha um iten."));
                        return;
                    }

                    player.closeInventory();
                    conversationFactory.create()
                        .withFirstPrompt(new RewardChanceConversation(context))
                        .withTimeout(15)
                        .withLocalEcho(false)
                        .buildConversation(player)
                        .begin();
                }
            )
            .item(
                27,
                ItemBuilder.of()
                    .material(Material.ARROW)
                    .name(color("&eVoltar"))
                    .build(),
                click -> Views.get().open(player, CrateRewardsPageView.class, new CrateRewardsPageView.Context(
                    context.id, context.page
                ))
            )
            .build(viewContext, player, data);
    }

    @Override
    public void onClick(@NotNull ViewClick click) {
        if (click.clickPosition() == ClickPosition.NONE) {
            return;
        }

        if (click.clickPosition() == ClickPosition.TOP) {
            ClickHandler.handleTopNonNull(viewContext, click);
            return;
        }

        click.cancel();

        if (click.clickedItem() == null || click.clickedItem().getType() == Material.AIR) {
            return;
        }

        final Context context = (Context) requireNonNull(viewContext.getViewer(click.whoClicked().getUniqueId()).data());
        Views.get().open(click.whoClicked(), CrateRewardEditView.class, context.changeReward(new ChanceReward(
            click.clickedItem().clone(),
            context.currentReward == null ? 0.0 : context.currentReward.chance()
        )));
    }

    @Override
    public void onClose(@NotNull ViewClose close) {
        final Context context = (Context) requireNonNull(viewContext.getViewer(close.whoCloses().getUniqueId()).data());
        viewContext.removeViewer(close.whoCloses().getUniqueId());

        if (context.currentReward == null) {
            return;
        }

        itemStorage.saveItem(new StorageKey(context.id, context.page, context.slot), context.currentReward);
    }
}
