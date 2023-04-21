package com.worldplugins.caixas.config.data.animation;

import lombok.NonNull;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Arrays;

public enum AnimationType {
    DROP("DROP") {
        @Override
        public @NonNull AnimationFactory getFactory(@NonNull ConfigurationSection section) {
            return new DropAnimationFactory(section);
        }
    },
    EFFECT("EFEITO") {
        @Override
        public @NonNull AnimationFactory getFactory(@NonNull ConfigurationSection section) {
            return new EffectAnimationFactory(section);
        }
    },
    BLOCK_MUTATION("MUTACAO_BLOCO") {
        @Override
        public @NonNull AnimationFactory getFactory(@NonNull ConfigurationSection section) {
            return new BlockMutationAnimationFactory(section);
        }
    };

    private final @NonNull String configName;

    AnimationType(@NonNull String configName) {
        this.configName = configName;
    }

    public abstract @NonNull AnimationFactory getFactory(@NonNull ConfigurationSection section);

    public static AnimationType find(@NonNull String name) {
        return Arrays.stream(values())
            .filter(type -> type.configName.equals(name))
            .findFirst()
            .orElse(null);
    }
}
