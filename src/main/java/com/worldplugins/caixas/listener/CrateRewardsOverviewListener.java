package com.worldplugins.caixas.listener;

import com.worldplugins.caixas.controller.RewardsController;
import com.worldplugins.caixas.manager.CrateManager;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

@RequiredArgsConstructor
public class CrateRewardsOverviewListener implements Listener {
    private final @NonNull CrateManager crateManager;
    private final @NonNull RewardsController rewardsController;

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_BLOCK)
            return;

        final CrateManager.LocatedCrate crate = crateManager.getLocatedCrate(
            event.getClickedBlock().getLocation()
        );

        if (crate == null)
            return;

        rewardsController.updateView(event.getPlayer(), crate.getId(), 0);
    }
}
