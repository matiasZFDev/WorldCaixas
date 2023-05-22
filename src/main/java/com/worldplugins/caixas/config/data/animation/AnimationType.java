package com.worldplugins.caixas.config.data.animation;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public enum AnimationType {
    DROP("DROP") {
        @Override
        public @NotNull AnimationFactory getFactory(@NotNull ConfigurationSection section) {
            return new DropAnimationFactory(section);
        }
    },
    EFFECT("EFEITO") {
        @Override
        public @NotNull AnimationFactory getFactory(@NotNull ConfigurationSection section) {
            return new EffectAnimationFactory(section);
        }
    },
    BLOCK_MUTATION("MUTACAO_BLOCO") {
        @Override
        public @NotNull AnimationFactory getFactory(@NotNull ConfigurationSection section) {
            return new BlockMutationAnimationFactory(section);
        }
    };

    private final @NotNull String configName;

    AnimationType(@NotNull String configName) {
        this.configName = configName;
    }

    public abstract @NotNull AnimationFactory getFactory(@NotNull ConfigurationSection section);

    public static AnimationType find(@NotNull String name) {
        return Arrays.stream(values())
            .filter(type -> type.configName.equals(name))
            .findFirst()
            .orElse(null);
    }
}
