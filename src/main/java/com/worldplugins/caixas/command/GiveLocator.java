package com.worldplugins.caixas.command;

import com.worldplugins.caixas.NBTKeys;
import com.worldplugins.caixas.config.MainConfig;
import com.worldplugins.caixas.extension.ResponseExtensions;
import com.worldplugins.lib.command.CommandModule;
import com.worldplugins.lib.command.CommandTarget;
import com.worldplugins.lib.command.annotation.ArgsChecker;
import com.worldplugins.lib.command.annotation.Command;
import com.worldplugins.lib.extension.GenericExtensions;
import com.worldplugins.lib.extension.bukkit.NBTExtensions;
import com.worldplugins.lib.extension.bukkit.PlayerExtensions;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;
import java.util.stream.Collectors;

@ExtensionMethod({
    NBTExtensions.class,
    ResponseExtensions.class,
    GenericExtensions.class,
    PlayerExtensions.class
})

@RequiredArgsConstructor
public class GiveLocator implements CommandModule {
    private final @NonNull MainConfig mainConfig;

    @Command(
        name = "caixas localizador",
        usage = "&cArgumentos invalidos. Digite /caixas localizador <tipo>.",
        argsChecks = {@ArgsChecker(size = 1)},
        target = CommandTarget.PLAYER,
        permission = "worldcaixas.localizar"
    )
    @Override
    public void execute(@NonNull CommandSender sender, @NonNull String[] args) {
        final Player player = (Player) sender;
        final String crateId = args[0];
        final Optional<MainConfig.Config.Crate> crate = mainConfig.get().getCrates().getById(crateId);

        if (!crate.isPresent()) {
            final String types = mainConfig.get().getCrates().getAll().stream()
                .map(MainConfig.Config.Crate::getId)
                .collect(Collectors.joining());
            player.respond("Caixa-inexistente", message -> message.replace(
                "@tipo".to(crateId),
                "@lista".to(types)
            ));
            return;
        }

        final ItemStack locatorItem = mainConfig.get().getLocatorItem()
            .clone()
            .addReference(NBTKeys.LOCATOR_KEY, crateId);
        player.giveItems(locatorItem);
        player.respond("Localizador-entrego", message -> message.replace(
            "@tipo".to(crateId)
        ));
    }
}
