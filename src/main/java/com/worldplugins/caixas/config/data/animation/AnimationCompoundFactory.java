package com.worldplugins.caixas.config.data.animation;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class AnimationCompoundFactory implements AnimationFactory {
    private final @NonNull Collection<AnimationFactory> factories;

    @Override
    public @NonNull Animation create(@NonNull Location origin) {
        final List<Animation> animations = factories.stream()
            .map(factory -> factory.create(origin))
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
