package com.worldplugins.caixas.command;

import com.worldplugins.caixas.config.data.MainData;
import com.worldplugins.caixas.factory.KeyFactory;
import me.post.lib.command.CommandModule;
import me.post.lib.command.annotation.Command;
import me.post.lib.config.model.ConfigModel;
import me.post.lib.util.NumberFormats;
import me.post.lib.util.Players;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;

import static com.worldplugins.caixas.Response.respond;
import static me.post.lib.util.Pairs.to;

public class GiveKeyAllCommand implements CommandModule {
    private final @NotNull KeyFactory keyFactory;
    private final @NotNull ConfigModel<MainData> mainConfig;

    public GiveKeyAllCommand(@NotNull KeyFactory keyFactory, @NotNull ConfigModel<MainData> mainConfig) {
        this.keyFactory = keyFactory;
        this.mainConfig = mainConfig;
    }

    @Command(name = "caixas darkeyall")
    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String[] args) {
        if (!sender.hasPermission("worldcaixas.darkeyall")) {
            respond(sender, "Dar-key-all-permissoes");
            return;
        }

        if (args.length != 2) {
            respond(sender, "Dar-key-all-uso");
            return;
        }

        if (!NumberFormats.isValidNumber(args[1])) {
            respond(sender, "Quantia-invalida");
            return;
        }

        final int amount = Integer.parseInt(args[1]);
        final String crateId = args[0];
        final ItemStack keyItem = keyFactory.create(crateId);

        if (keyItem == null) {
            final String types = mainConfig.data().crates().getAll().stream()
                .map(MainData.Crate::id)
                .collect(Collectors.joining(", "));
            respond(sender, "Caixa-inexistente", message -> message.replace(
                to("@tipo", crateId),
                to("@lista", types)
            ));
            return;
        }

        keyItem.setAmount(amount);
        Bukkit.getOnlinePlayers().forEach(player -> Players.giveItems(player, keyItem.clone()));
        respond(sender, "Chaves-enviadas-todos", message -> message.replace(
            to("@tipo", crateId),
            to("@quantia", NumberFormats.suffixed(amount))
        ));
    }
}
