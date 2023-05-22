package com.worldplugins.caixas.command;

import com.worldplugins.caixas.NBTKeys;
import com.worldplugins.caixas.config.data.MainData;
import me.post.lib.command.CommandModule;
import me.post.lib.command.annotation.Command;
import me.post.lib.config.model.ConfigModel;
import me.post.lib.util.NBTs;
import me.post.lib.util.Players;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;

import static com.worldplugins.caixas.Response.respond;
import static me.post.lib.util.Pairs.to;

public class GiveLocatorCommand implements CommandModule {
    private final @NotNull ConfigModel<MainData> mainConfig;

    public GiveLocatorCommand(@NotNull ConfigModel<MainData> mainConfig) {
        this.mainConfig = mainConfig;
    }


    @Command(name = "caixas localizador")
    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String[] args) {
        if (!sender.hasPermission("worldcaixas.localizar")) {
            respond(sender, "Obter-localizador-permissoes");
            return;
        }

        if (args.length != 1) {
            respond(sender, "Obter-localizador-uso");
            return;
        }

        final Player player = (Player) sender;
        final String crateId = args[0];
        final MainData.Crate crate = mainConfig.data().crates().getById(crateId);

        if (crate == null) {
            final String types = mainConfig.data().crates().getAll().stream()
                .map(MainData.Crate::id)
                .collect(Collectors.joining(", "));
            respond(player, "Caixa-inexistente", message -> message.replace(
                to("@tipo", crateId),
                to("@lista", types)
            ));
            return;
        }

        final ItemStack locatorItem = NBTs.modifyTags(mainConfig.data().locatorItem().clone(), nbtItem ->
            nbtItem.setString(NBTKeys.CRATE_LOCATOR, crateId)
        );

        Players.giveItems(player, locatorItem);
        respond(player, "Localizador-entrego", message -> message.replace(
            to("@tipo", crateId)
        ));
    }
}
