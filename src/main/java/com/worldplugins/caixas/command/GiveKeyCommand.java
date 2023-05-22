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
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;

import static com.worldplugins.caixas.Response.respond;
import static me.post.lib.util.Pairs.to;

public class GiveKeyCommand implements CommandModule {
    private final @NotNull KeyFactory keyFactory;
    private final @NotNull ConfigModel<MainData> mainConfig;

    public GiveKeyCommand(@NotNull KeyFactory keyFactory, @NotNull ConfigModel<MainData> mainConfig) {
        this.keyFactory = keyFactory;
        this.mainConfig = mainConfig;
    }

    @Command(name = "caixas darkey")
    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String[] args) {
        final Player receiver = Bukkit.getPlayer(args[0]);

        if (!sender.hasPermission("worldcaixas.darkey")) {
            respond(sender, "Dar-keys-permissoes");
            return;
        }

        if (args.length != 3) {
            respond(sender, "Dar-keys-uso");
            return;
        }

        if (receiver == null) {
            respond(sender, "Jogador-offline", message -> message.replace(
                to("@jogador", args[0])
            ));
            return;
        }

        if (!NumberFormats.isValidNumber(args[2])) {
            respond(sender, "Quantia-invalida");
            return;
        }

        final Integer amount = Integer.parseInt(args[2]);
        final String crateId = args[1];
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
        Players.giveItems(receiver, keyItem);

        respond(sender, "Chaves-enviadas", message -> message.replace(
            to("@tipo", crateId),
            to("@quantia", NumberFormats.suffixed(amount)),
            to("@jogador", receiver.getName())
        ));
        respond(sender, "Chaves-recebidas", message -> message.replace(
            to("@tipo", crateId),
            to("@quantia", NumberFormats.suffixed(amount))
        ));
    }
}
