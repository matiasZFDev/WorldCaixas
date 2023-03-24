package com.worldplugins.caixas.config.data.animation;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

@RequiredArgsConstructor
public final class EffectAnimationFactory implements AnimationFactory {
    private final @NonNull Plugin plugin;
    private final @NonNull ConfigurationSection section;

    @Override
    public @NonNull Animation create(@NonNull Location origin) {
        final ConfigurationSection positionSection = section.getConfigurationSection("Posicao");
        final Vector position = new Vector(
            positionSection.getDouble("X"),
            positionSection.getDouble("Y"),
            positionSection.getDouble("z")
        );
        final Location location = origin.clone().add(0.5, 0.5, 0.5).add(position);
        final Effect effect = Effect.valueOf(this.section.getString("Id"));
        final short repetition = (short) section.getInt("Repeticao");
        return new EffectAnimation(plugin, location, effect, repetition);
    }

    @RequiredArgsConstructor
    public static final class EffectAnimation implements Animation {
        private final @NonNull Plugin plugin;
        private final @NonNull Location location;
        private final @NonNull Effect effect;
        private final short repetition;
        private int taskId = -1;
        private short nextEffect;

        public void run() {
            taskId = Bukkit.getScheduler().runTaskTimer(this.plugin, this::animation, 20L, 20L).getTaskId();
        }

        private void animation() {
            if (this.nextEffect > 0) {
                this.nextEffect -= 1;
            } else {
                this.location.getWorld().playEffect(this.location, this.effect, 0);
                this.nextEffect = this.repetition;
            }
        }

        public void stop() {
            Bukkit.getScheduler().cancelTask(taskId);
        }
    }
}