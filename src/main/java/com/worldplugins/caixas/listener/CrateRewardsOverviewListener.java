package com.worldplugins.caixas.listener;

import com.worldplugins.caixas.manager.CrateManager;
import com.worldplugins.caixas.view.CrateRewardsView;
import me.post.lib.view.Views;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

public class CrateRewardsOverviewListener implements Listener {
    private final @NotNull CrateManager crateManager;

    public CrateRewardsOverviewListener(@NotNull CrateManager crateManager) {
        this.crateManager = crateManager;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        if (event.getPlayer().isSneaking()) {
            return;
        }

        final CrateManager.LocatedCrate crate = crateManager.getLocatedCrate(
            event.getClickedBlock().getLocation()
        );

        if (crate == null) {
            return;
        }

        Views.get().open(event.getPlayer(), CrateRewardsView.class, new CrateRewardsView.Context(crate.id(), 0));
    }
}
