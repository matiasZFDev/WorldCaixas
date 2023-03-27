package com.worldplugins.caixas.command;

import com.worldplugins.caixas.factory.KeyFactory;
import com.worldplugins.caixas.config.MainConfig;
import com.worldplugins.caixas.extension.ResponseExtensions;
import com.worldplugins.lib.command.CommandModule;
import com.worldplugins.lib.command.annotation.ArgsChecker;
import com.worldplugins.lib.command.annotation.Command;
import com.worldplugins.lib.extension.GenericExtensions;
import com.worldplugins.lib.extension.NumberFormatExtensions;
import com.worldplugins.lib.extension.bukkit.PlayerExtensions;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;
import java.util.stream.Collectors;

@ExtensionMethod({
    ResponseExtensions.class,
    NumberFormatExtensions.class,
    GenericExtensions.class,
    PlayerExtensions.class
})

@RequiredArgsConstructor
public class GiveKeyAll implements CommandModule {
    private final @NonNull KeyFactory keyFactory;
    private final @NonNull MainConfig mainConfig;

    @Command(
        name = "caixas darkeyall",
        usage = "&cArgumentos invalidos. Digite /caixas darkeyall <caixa> <quantia>",
        argsChecks = {@ArgsChecker(size = 2)},
        permission = "worldcaixas.darkeyall"
    )
    @Override
    public void execute(@NonNull CommandSender sender, @NonNull String[] args) {
        if (!args[1].isValidNumber()) {
            sender.respond("Quantia-invalida");
            return;
        }

        final Integer amount = Integer.parseInt(args[1]);
        final String crateId = args[0];
        final Optional<ItemStack> keyItem = keyFactory.create(crateId);

        if (!keyItem.isPresent()) {
            final String types = mainConfig.get().getCrates().getAll().stream()
                .map(MainConfig.Config.Crate::getId)
                .collect(Collectors.joining());
            sender.respond("Caixa-inexistente", message -> message.replace(
                "@tipo".to(crateId),
                "@lista".to(types)
            ));
            return;
        }

        keyItem.get().setAmount(amount);
        Bukkit.getOnlinePlayers().forEach(player -> player.giveItems(keyItem.get().clone()));
        sender.respond("Chaves-enviadas-todos", message -> message.replace(
            "@tipo".to(crateId),
            "@quantia".to(amount.suffixed())
        ));
    }
}
