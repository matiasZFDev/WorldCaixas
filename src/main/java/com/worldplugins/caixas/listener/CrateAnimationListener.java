package com.worldplugins.caixas.listener;

import com.worldplugins.caixas.NBTKeys;
import com.worldplugins.lib.extension.bukkit.NBTExtensions;
import lombok.experimental.ExtensionMethod;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;

@ExtensionMethod({
    NBTExtensions.class
})

public class CrateAnimationListener implements Listener {
    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent event) {
        if (!event.getItem().getItemStack().hasReference(NBTKeys.FAKE_DROP))
            return;

        event.setCancelled(true);
    }
}
