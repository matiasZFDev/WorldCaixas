package com.worldplugins.caixas.util;

import com.worldplugins.lib.common.Factory;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.plugin.Plugin;

@RequiredArgsConstructor
public class ConversationProvider implements Factory<ConversationFactory> {
    private final @NonNull Plugin plugin;

    public @NonNull ConversationFactory create() {
        return new ConversationFactory(plugin);
    }
}
