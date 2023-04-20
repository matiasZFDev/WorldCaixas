package com.worldplugins.caixas.command;

import com.worldplugins.caixas.NBTKeys;
import com.worldplugins.caixas.config.data.MainData;
import com.worldplugins.caixas.extension.ResponseExtensions;
import com.worldplugins.lib.command.CommandModule;
import com.worldplugins.lib.command.CommandTarget;
import com.worldplugins.lib.command.annotation.ArgsChecker;
import com.worldplugins.lib.command.annotation.Command;
import com.worldplugins.lib.config.cache.ConfigCache;
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
    private final @NonNull ConfigCache<MainData> mainConfig;

    @Command(
        name = "caixas localizador",
        usage = "&cArgumentos invalidos. Digite /caixas localizador <caixa>.",
        argsChecks = {@ArgsChecker(size = 1)},
        target = CommandTarget.PLAYER,
        permission = "worldcaixas.localizar"
    )
    @Override
    public void execute(@NonNull CommandSender sender, @NonNull String[] args) {
        final Player player = (Player) sender;
        final String crateId = args[0];
        final Optional<MainData.Crate> crate = mainConfig.data().getCrates().getById(crateId);

        if (!crate.isPresent()) {
            final String types = mainConfig.data().getCrates().getAll().stream()
                .map(MainData.Crate::getId)
                .collect(Collectors.joining(", "));
            player.respond("Caixa-inexistente", message -> message.replace(
                "@tipo".to(crateId),
                "@lista".to(types)
            ));
            return;
        }

        final ItemStack locatorItem = mainConfig.data().getLocatorItem()
            .clone()
            .addReference(NBTKeys.CRATE_LOCATOR, crateId);
        player.giveItems(locatorItem);
        player.respond("Localizador-entrego", message -> message.replace(
            "@tipo".to(crateId)
        ));
    }
}
