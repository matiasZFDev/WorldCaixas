package com.worldplugins.caixas.config.data.animation;

import me.post.lib.util.Scheduler;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class AnimationCompoundFactory implements AnimationFactory {
    private final @NotNull Collection<AnimationFactory> factories;

    public AnimationCompoundFactory(@NotNull Collection<AnimationFactory> factories) {
        this.factories = factories;
    }

    @Override
    public @NotNull Animation create(@NotNull Scheduler scheduler, @NotNull Location origin) {
        final List<Animation> animations = factories.stream()
            .map(factory -> factory.create(scheduler, origin))
            .collect(Collectors.toList());

        return new Animation() {
            @Override
            public void run() {
                animations.forEach(Animation::run);
            }

            @Override
            public void stop() {
                animations.forEach(Animation::stop);
            }
        };
    }
}
