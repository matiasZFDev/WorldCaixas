package com.worldplugins.caixas.config.data;

import com.worldplugins.caixas.config.data.animation.AnimationFactory;
import com.worldplugins.caixas.config.data.animation.BlockMutationAnimationFactory;
import com.worldplugins.caixas.config.data.animation.DropAnimationFactory;
import com.worldplugins.caixas.config.data.animation.EffectAnimationFactory;
import lombok.NonNull;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Arrays;
import java.util.Optional;

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

    public static @NonNull Optional<AnimationType> find(@NonNull String name) {
        return Arrays.stream(values())
            .filter(type -> type.configName.equals(name))
            .findFirst();
    }
}
