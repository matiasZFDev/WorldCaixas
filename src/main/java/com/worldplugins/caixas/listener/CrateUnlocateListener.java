package com.worldplugins.caixas.listener;

import com.worldplugins.caixas.manager.CrateManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;

import static com.worldplugins.caixas.Response.respond;

public class CrateUnlocateListener implements Listener {
    private final @NotNull CrateManager crateManager;

    public CrateUnlocateListener(@NotNull CrateManager crateManager) {
        this.crateManager = crateManager;
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        final CrateManager.LocatedCrate crate = crateManager.getLocatedCrate(event.getBlock().getLocation());

        if (crate == null) {
            return;
        }

        final Player player = event.getPlayer();

        if (!player.hasPermission("worldcaixas.retirarcaixa")) {
            event.setCancelled(true);
            respond(player, "Retirar-caixa-permissoes");
            return;
        }

        if (!player.isSneaking()) {
            event.setCancelled(true);
            respond(player, "Retirar-caixa-shift");
            return;
        }

        crateManager.unlocateCrate(crate);
        respond(player, "Caixa-retirada");
    }
}
