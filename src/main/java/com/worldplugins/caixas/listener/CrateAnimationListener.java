package com.worldplugins.caixas.listener;

import com.worldplugins.caixas.NBTKeys;
import me.post.lib.util.NBTs;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class CrateAnimationListener implements Listener {
    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent event) {
        if (!NBTs.hasTag(event.getItem().getItemStack(), NBTKeys.FAKE_DROP)) {
            return;
        }

        event.setCancelled(true);
    }
}
