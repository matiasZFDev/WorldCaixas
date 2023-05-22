package com.worldplugins.caixas.command;

import me.post.lib.command.CommandModule;
import me.post.lib.command.annotation.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static com.worldplugins.caixas.Response.respond;

public class HelpCommand implements CommandModule {
    @Command(name = "caixas ajuda")
    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String[] args) {
        respond(sender, sender.hasPermission("worldcaixas.ajudastaff") ? "Ajuda-staff" : "Ajuda");
    }
}
