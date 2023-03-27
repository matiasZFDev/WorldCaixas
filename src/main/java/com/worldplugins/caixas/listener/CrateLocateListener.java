package com.worldplugins.caixas.listener;

import com.worldplugins.caixas.NBTKeys;
import com.worldplugins.caixas.config.MainConfig;
import com.worldplugins.caixas.extension.ResponseExtensions;
import com.worldplugins.caixas.manager.CrateManager;
import com.worldplugins.lib.extension.GenericExtensions;
import com.worldplugins.lib.extension.bukkit.NBTExtensions;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Optional;

@ExtensionMethod({
    NBTExtensions.class,
    ResponseExtensions.class,
    GenericExtensions.class
})

@RequiredArgsConstructor
public class CrateLocateListener implements Listener {
    private final @NonNull MainConfig mainConfig;
    private final @NonNull CrateManager crateManager;

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!event.hasItem() || event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        if (!event.getItem().hasReference(NBTKeys.CRATE_LOCATOR))
            return;

        event.setCancelled(true);

        final Player player = event.getPlayer();
        final String crateId = event.getItem().getReference(NBTKeys.CRATE_LOCATOR);
        final Optional<MainConfig.Config.Crate> crate = mainConfig.get().getCrates().getById(crateId);

        if (!crate.isPresent()) {
            final String crates = mainConfig.get().getCrates().getAll().toString();
            player.respond("Crate-inexistente", message -> message.replace(
                "@tipo".to(crateId),
                "@lista".to(crates)
            ));
            return;
        }

        final BlockFace blockFace = event.getBlockFace();
        final Location location = event.getClickedBlock().getLocation().clone().add(
            blockFace.getModX(), blockFace.getModY(), blockFace.getModZ()
        );
        crateManager.locateCrate(location, crate.get());
        player.respond("Caixa-localizada");
    }
}
