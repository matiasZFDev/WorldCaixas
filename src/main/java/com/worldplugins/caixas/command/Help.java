package com.worldplugins.caixas.command;

import com.worldplugins.caixas.extension.ResponseExtensions;
import com.worldplugins.lib.command.CommandModule;
import com.worldplugins.lib.command.annotation.Command;
import lombok.NonNull;
import lombok.experimental.ExtensionMethod;
import org.bukkit.command.CommandSender;

@ExtensionMethod({
    ResponseExtensions.class
})

public class Help implements CommandModule {
    @Command(
        name = "caixas ajuda",
        usage = "&cArgumentos invalidos. Digite /caixas ajuda"
    )
    @Override
    public void execute(@NonNull CommandSender sender, @NonNull String[] args) {
        if (sender.hasPermission("worldcaixas.ajudastaff"))
            sender.respond("Ajuda-staff");
        else
            sender.respond("Ajuda");
    }
}
