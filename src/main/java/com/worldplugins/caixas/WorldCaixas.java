package com.worldplugins.caixas;

import org.bukkit.plugin.java.JavaPlugin;

public class WorldCaixas extends JavaPlugin {
    private Runnable onDisable;

    @Override
    public void onEnable() {
        onDisable = new PluginExecutor(this).execute();
    }

    @Override
    public void onDisable() {
        if (onDisable != null)
            onDisable.run();
    }
}
