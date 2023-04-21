package com.worldplugins.caixas.command;

import com.worldplugins.caixas.config.data.MainData;
import com.worldplugins.caixas.factory.KeyFactory;
import com.worldplugins.caixas.extension.ResponseExtensions;
import com.worldplugins.lib.command.CommandModule;
import com.worldplugins.lib.command.annotation.ArgsChecker;
import com.worldplugins.lib.command.annotation.Command;
import com.worldplugins.lib.config.cache.ConfigCache;
import com.worldplugins.lib.extension.GenericExtensions;
import com.worldplugins.lib.extension.NumberFormatExtensions;
import com.worldplugins.lib.extension.bukkit.PlayerExtensions;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.stream.Collectors;

@ExtensionMethod({
    ResponseExtensions.class,
    NumberFormatExtensions.class,
    GenericExtensions.class,
    PlayerExtensions.class
})

@RequiredArgsConstructor
public class GiveKey implements CommandModule {
    private final @NonNull KeyFactory keyFactory;
    private final @NonNull ConfigCache<MainData> mainConfig;

    @Command(
        name = "caixas darkey",
        usage = "&cArgumentos invalidos. Digite /caixas darkey <jogador> <caixa> <quantia>",
        argsChecks = {@ArgsChecker(size = 3)},
        permission = "worldcaixas.darkey"
    )
    @Override
    public void execute(@NonNull CommandSender sender, @NonNull String[] args) {
        final Player receiver = Bukkit.getPlayer(args[0]);

        if (receiver == null) {
            sender.respond("Jogador-offline");
            return;
        }

        if (!args[2].isValidNumber()) {
            sender.respond("Quantia-invalida");
            return;
        }

        final Integer amount = Integer.parseInt(args[2]);
        final String crateId = args[1];
        final ItemStack keyItem = keyFactory.create(crateId);

        if (keyItem == null) {
            final String types = mainConfig.data().getCrates().getAll().stream()
                .map(MainData.Crate::getId)
                .collect(Collectors.joining(", "));
            sender.respond("Caixa-inexistente", message -> message.replace(
                "@tipo".to(crateId),
                "@lista".to(types)
            ));
            return;
        }

        keyItem.setAmount(amount);
        receiver.giveItems(keyItem);
        sender.respond("Chaves-enviadas", message -> message.replace(
            "@tipo".to(crateId),
            "@quantia".to(amount.suffixed()),
            "@jogador".to(receiver.getName())
        ));
        receiver.respond("Chaves-recebidas", message -> message.replace(
            "@tipo".to(crateId),
            "@quantia".to(amount.suffixed())
        ));
    }
}
