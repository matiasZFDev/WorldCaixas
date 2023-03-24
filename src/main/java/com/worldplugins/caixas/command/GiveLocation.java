package com.worldplugins.caixas.command;

import com.worldplugins.caixas.config.MainConfig;
import com.worldplugins.lib.command.CommandModule;
import com.worldplugins.lib.command.CommandTarget;
import com.worldplugins.lib.command.annotation.Command;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class GiveLocation implements CommandModule {
    private final @NonNull MainConfig mainConfig;

    @Command(
        name = "caixas localizador",
        usage = "&cArgumentos invalidos. Digite /caixas localizador.",
        target = CommandTarget.PLAYER,
        permission = "worldcaixas.localizar"
    )
    @Override
    public void execute(@NonNull CommandSender sender, @NonNull String[] args) {
        final Player player = (Player) sender;

    }
}
