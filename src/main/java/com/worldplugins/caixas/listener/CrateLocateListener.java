package com.worldplugins.caixas.listener;

import com.worldplugins.caixas.NBTKeys;
import com.worldplugins.caixas.config.data.MainData;
import com.worldplugins.caixas.manager.CrateManager;
import me.post.deps.nbt_api.nbtapi.NBTCompound;
import me.post.lib.config.model.ConfigModel;
import me.post.lib.util.NBTs;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

import static com.worldplugins.caixas.Response.respond;
import static me.post.lib.util.Pairs.to;

public class CrateLocateListener implements Listener {
    private final @NotNull ConfigModel<MainData> mainConfig;
    private final @NotNull CrateManager crateManager;

    public CrateLocateListener(@NotNull ConfigModel<MainData> mainConfig, @NotNull CrateManager crateManager) {
        this.mainConfig = mainConfig;
        this.crateManager = crateManager;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!event.hasItem() || event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (!NBTs.hasTag(event.getItem(), NBTKeys.CRATE_LOCATOR)) {
            return;
        }

        event.setCancelled(true);

        final Player player = event.getPlayer();
        final String crateId = NBTs.getTagValue(event.getItem(), NBTKeys.CRATE_LOCATOR, NBTCompound::getString);
        final MainData.Crate crate = mainConfig.data().crates().getById(crateId);

        if (crate == null) {
            final String crates = mainConfig.data().crates().getAll().toString();
            respond(player, "Crate-inexistente", message -> message.replace(
                to("@tipo", crateId),
                to("@lista", crates)
            ));
            return;
        }

        final BlockFace blockFace = event.getBlockFace();
        final Location location = event.getClickedBlock().getLocation().clone().add(
            blockFace.getModX(), blockFace.getModY(), blockFace.getModZ()
        );
        crateManager.locateCrate(location, crate);
        respond(player, "Caixa-localizada");
    }
}
