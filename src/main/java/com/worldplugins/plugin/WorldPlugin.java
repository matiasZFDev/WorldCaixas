package com.worldplugins.plugin;

import org.bukkit.plugin.java.JavaPlugin;

public class WorldPlugin extends JavaPlugin {
    private Runnable onDisable;

    @Override
    public void onEnable() {
        onDisable = new PluginWaiter(this).execute();
    }

    @Override
    public void onDisable() {
        if (onDisable != null)
            onDisable.run();
    }
}
