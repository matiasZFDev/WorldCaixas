package com.worldplugins.caixas.conversation;

import com.worldplugins.caixas.config.data.storage.ChanceReward;
import com.worldplugins.caixas.view.CrateRewardEditView;
import me.post.lib.util.Colors;
import me.post.lib.util.NumberFormats;
import me.post.lib.view.Views;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static com.worldplugins.caixas.Response.respond;

public class RewardChanceConversation extends StringPrompt {
    private final @NotNull CrateRewardEditView.Context currentContext;

    public RewardChanceConversation(@NotNull CrateRewardEditView.Context currentContext) {
        this.currentContext = currentContext;
    }

    @Override
    public String getPromptText(ConversationContext context) {
        return Colors.color("&eInsira a chance dessa recompensa:");
    }

    @Override
    public Prompt acceptInput(ConversationContext context, String value) {
        final Player player = (Player) context.getForWhom();

        if (value.equals("CANCELAR")) {
            respond(player, "Operacao-cancelada");
            return null;
        }

        final Double chance = NumberFormats.isValidDecimal(value)
            ? Double.parseDouble(value)
            : null;

        if (chance == null) {
            respond(player, "Recompensa-editar-chance-invalida");
            return null;
        }

        if (currentContext.currentReward() == null) {
            player.sendMessage("&cOcorreu um erro. Tente novamente.");
            Views.get().open(player, CrateRewardEditView.class, currentContext.changeReward(null));
            return null;
        }

        Views.get().open(player, CrateRewardEditView.class, currentContext.changeReward(
            new ChanceReward(currentContext.currentReward().bukkitItem() , chance)
        ));
        return null;
    }
}
