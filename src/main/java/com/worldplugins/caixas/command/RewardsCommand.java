package com.worldplugins.caixas.command;

import com.worldplugins.caixas.config.data.MainData;
import com.worldplugins.caixas.view.CrateRewardsPageView;
import me.post.lib.command.CommandModule;
import me.post.lib.command.annotation.Command;
import me.post.lib.config.model.ConfigModel;
import me.post.lib.view.Views;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;

import static com.worldplugins.caixas.Response.respond;
import static me.post.lib.util.Pairs.to;

public class RewardsCommand implements CommandModule {
    private final @NotNull ConfigModel<MainData> mainConfig;

    public RewardsCommand(@NotNull ConfigModel<MainData> mainConfig) {
        this.mainConfig = mainConfig;
    }

    @Command(name = "caixas recompensas")
    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String[] args) {
        if (!sender.hasPermission("worldcaixas.recompensas")) {
            respond(sender, "Recompensas-permissoes");
            return;
        }

        if (args.length != 1) {
            respond(sender, "Recompensas-uso");
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

        Views.get().open(player, CrateRewardsPageView.class, new CrateRewardsPageView.Context(crateId, 0));
    }
}
