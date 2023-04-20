package com.worldplugins.caixas.listener;

import com.worldplugins.caixas.CrateRewardHelper;
import com.worldplugins.caixas.NBTKeys;
import com.worldplugins.caixas.extension.ResponseExtensions;
import com.worldplugins.caixas.manager.CrateManager;
import com.worldplugins.lib.extension.GenericExtensions;
import com.worldplugins.lib.extension.bukkit.NBTExtensions;
import com.worldplugins.lib.extension.bukkit.PlayerExtensions;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

@ExtensionMethod({
    NBTExtensions.class,
    ResponseExtensions.class,
    GenericExtensions.class,
    PlayerExtensions.class
})

@RequiredArgsConstructor
public class CrateOpenListener implements Listener {
    private final @NonNull CrateManager crateManager;
    private final @NonNull CrateRewardHelper crateRewardHelper;

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getItem() == null || event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        if (!event.getItem().hasReference(NBTKeys.CRATE_KEY))
            return;

        event.setCancelled(true);

        final Player player = event.getPlayer();
        final Optional<CrateManager.LocatedCrate> crate = crateManager.getLocatedCrate(
            event.getClickedBlock().getLocation()
        );

        if (!crate.isPresent())
            return;

        final String crateId = crate.get().getId();

        if (player.isSneaking()) {
            final int keys = event.getItem().getAmount();
            player.setItemInHand(null);

            for (int i = 0; i < keys; i++) {
                openCrate(crateId, player);
            }
        } else {
            player.reduceHandItem();
            openCrate(crateId, player);
        }
    }

    private void openCrate(@NonNull String crateId, @NonNull Player player) {
        final Optional<ItemStack> reward = crateRewardHelper.openCrate(crateId);

        if (!reward.isPresent()) {
            player.respond("Caixa-aberta-nada");
            return;
        }

        player.giveItems(reward.get());

        if (reward.get().hasItemMeta() && reward.get().getItemMeta().hasDisplayName())
            player.respond("Caixa-aberta", message -> message.replace(
                "@recompensa".to(reward.get().getItemMeta().getDisplayName()),
                "@quantia".to(String.valueOf(reward.get().getAmount()))
            ));
        else
            player.respond("Caixa-aberta-sem-nome", message -> message.replace(
                "@quantia".to(String.valueOf(reward.get().getAmount()))
            ));
    }
}
