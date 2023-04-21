package com.worldplugins.caixas.command;

import com.worldplugins.caixas.config.data.MainData;
import com.worldplugins.caixas.extension.ResponseExtensions;
import com.worldplugins.caixas.extension.ViewExtensions;
import com.worldplugins.caixas.view.CrateRewardsPageView;
import com.worldplugins.lib.command.CommandModule;
import com.worldplugins.lib.command.CommandTarget;
import com.worldplugins.lib.command.annotation.ArgsChecker;
import com.worldplugins.lib.command.annotation.Command;
import com.worldplugins.lib.config.cache.ConfigCache;
import com.worldplugins.lib.extension.GenericExtensions;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

@ExtensionMethod({
    ResponseExtensions.class,
    GenericExtensions.class,
    ViewExtensions.class
})

@RequiredArgsConstructor
public class Rewards implements CommandModule {
    private final @NonNull ConfigCache<MainData> mainConfig;

    @Command(
        name = "caixas recompensas",
        target = CommandTarget.PLAYER,
        argsChecks = {@ArgsChecker(size = 1)},
        usage = "&cArgumentos invalidos. Digite /caixa recompensas <caixa>",
        permission = "worldcaixas.recompensas"
    )
    @Override
    public void execute(@NonNull CommandSender sender, @NonNull String[] args) {
        final Player player = (Player) sender;
        final String crateId = args[0];
        final MainData.Crate crate = mainConfig.data().getCrates().getById(crateId);

        if (crate == null) {
            final String types = mainConfig.data().getCrates().getAll().stream()
                .map(MainData.Crate::getId)
                .collect(Collectors.joining(", "));
            sender.respond("Caixa-inexistente", message -> message.replace(
                "@tipo".to(crateId),
                "@lista".to(types)
            ));
            return;
        }

        player.openView(CrateRewardsPageView.class, new CrateRewardsPageView.Context(
            0, crateId
        ));
    }
}
