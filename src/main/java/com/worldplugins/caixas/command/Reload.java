package com.worldplugins.caixas.command;

import com.worldplugins.caixas.extension.ResponseExtensions;
import com.worldplugins.lib.command.CommandModule;
import com.worldplugins.lib.command.annotation.Command;
import com.worldplugins.lib.common.Updatable;
import com.worldplugins.lib.manager.config.ConfigManager;
import lombok.NonNull;
import lombok.experimental.ExtensionMethod;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

@ExtensionMethod({
    ResponseExtensions.class
})

public class Reload implements CommandModule {
    private final @NonNull ConfigManager configManager;
    private final @NonNull Updatable[] dependants;

    public Reload(@NonNull ConfigManager configManager, @NonNull Updatable... dependants) {
        this.configManager = configManager;
        this.dependants = dependants;
    }

    @Command(
        name = "caixas reload",
        usage = "&cArgumentos invalidos. Digite /caixas reload",
        permission = "worldcaixas.reload"
    )
    @Override
    public void execute(@NonNull CommandSender sender, @NonNull String[] args) {
        configManager.reloadAll();
        Arrays.asList(dependants).forEach(Updatable::update);
        sender.respond("Reload-sucesso");
    }
}
