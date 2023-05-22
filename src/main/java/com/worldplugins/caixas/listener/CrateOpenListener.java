package com.worldplugins.caixas.listener;

import com.worldplugins.caixas.helper.CrateRewardHelper;
import com.worldplugins.caixas.NBTKeys;
import com.worldplugins.caixas.manager.CrateManager;
import me.post.lib.util.NBTs;
import me.post.lib.util.Players;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import static com.worldplugins.caixas.Response.respond;
import static me.post.lib.util.Pairs.to;

public class CrateOpenListener implements Listener {
    private final @NotNull CrateManager crateManager;
    private final @NotNull CrateRewardHelper crateRewardHelper;

    public CrateOpenListener(@NotNull CrateManager crateManager, @NotNull CrateRewardHelper crateRewardHelper) {
        this.crateManager = crateManager;
        this.crateRewardHelper = crateRewardHelper;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getItem() == null || event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (!NBTs.hasTag(event.getItem(), NBTKeys.CRATE_KEY)) {
            return;
        }

        event.setCancelled(true);

        final Player player = event.getPlayer();
        final CrateManager.LocatedCrate crate = crateManager.getLocatedCrate(
            event.getClickedBlock().getLocation()
        );

        if (crate == null) {
            return;
        }

        final String crateId = crate.id();

        if (player.isSneaking()) {
            final int keys = event.getItem().getAmount();
            player.setItemInHand(null);

            for (int i = 0; i < keys; i++) {
                openCrate(crateId, player);
            }
        } else {
            Players.reduceHandItem(player);
            openCrate(crateId, player);
        }
    }

    private void openCrate(@NotNull String crateId, @NotNull Player player) {
        final ItemStack reward = crateRewardHelper.openCrate(crateId);

        if (reward == null) {
            respond(player, "Caixa-aberta-nada");
            return;
        }

        Players.giveItems(player, reward);

        if (reward.hasItemMeta() && reward.getItemMeta().hasDisplayName()) {
            respond(player, "Caixa-aberta", message -> message.replace(
                    to("@recompensa", reward.getItemMeta().getDisplayName()),
                    to("@quantia", String.valueOf(reward.getAmount()))
            ));
        } else {
            respond(player, "Caixa-aberta-sem-nome", message -> message.replace(
                    to("@quantia", String.valueOf(reward.getAmount()))
            ));
        }
    }
}
