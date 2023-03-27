package com.worldplugins.caixas.conversation;

import com.worldplugins.caixas.extension.ResponseExtensions;
import com.worldplugins.caixas.extension.ViewExtensions;
import com.worldplugins.caixas.rewards.ChanceReward;
import com.worldplugins.caixas.view.CrateRewardEditView;
import com.worldplugins.lib.extension.bukkit.ColorExtensions;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

import java.util.Optional;

@ExtensionMethod({
    ColorExtensions.class,
    ResponseExtensions.class,
    ViewExtensions.class
})

@RequiredArgsConstructor
public class RewardChanceConversation extends StringPrompt {
    private final @NonNull CrateRewardEditView.Context currentContext;

    @Override
    public String getPromptText(ConversationContext context) {
        return "&eInsira a chance dessa recompensa:".color();
    }

    @Override
    public Prompt acceptInput(ConversationContext context, String value) {
        final Player player = (Player) context.getForWhom();

        if (value.equals("CANCELAR")) {
            player.respond("Operacao-cancelada");
            return null;
        }

        final Optional<Double> chance = parseDouble(value);

        if (!chance.isPresent()) {
            player.respond("Recompensa-editar-chance-invalida");
            return null;
        }

        player.openView(CrateRewardEditView.class, currentContext.changeReward(
            new ChanceReward(currentContext.getCurrentReward().getBukkitItem(), chance.get())
        ));
        return null;
    }

    private @NonNull Optional<Double> parseDouble(@NonNull String value) {
        try {
            return Optional.of(Double.parseDouble(value));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}