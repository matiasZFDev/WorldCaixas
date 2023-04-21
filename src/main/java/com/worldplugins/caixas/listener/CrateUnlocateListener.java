package com.worldplugins.caixas.listener;

import com.worldplugins.caixas.extension.ResponseExtensions;
import com.worldplugins.caixas.manager.CrateManager;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

@ExtensionMethod({
    ResponseExtensions.class
})

@RequiredArgsConstructor
public class CrateUnlocateListener implements Listener {
    private final @NonNull CrateManager crateManager;

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        final CrateManager.LocatedCrate crate = crateManager.getLocatedCrate(event.getBlock().getLocation());

        if (crate == null)
            return;

        if (!event.getPlayer().hasPermission("worldcaixas.retirarcaixa")) {
            event.setCancelled(true);
            event.getPlayer().respond("Retirar-caixa-permissoes");
            return;
        }

        if (!event.getPlayer().isSneaking()) {
            event.setCancelled(true);
            event.getPlayer().respond("Retirar-caixa-shift");
            return;
        }

        crateManager.unlocateCrate(crate);
        event.getPlayer().respond("Caixa-retirada");
    }
}
