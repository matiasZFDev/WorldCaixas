package com.worldplugins.caixas.config.data.animation;

import me.post.lib.util.Scheduler;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public final class EffectAnimationFactory implements AnimationFactory {
    private final @NotNull ConfigurationSection section;

    public EffectAnimationFactory(@NotNull ConfigurationSection section) {
        this.section = section;
    }

    @Override
    public @NotNull Animation create(@NotNull Scheduler scheduler, @NotNull Location origin) {
        final ConfigurationSection positionSection = section.getConfigurationSection("Posicao");
        final Vector position = new Vector(
            positionSection.getDouble("X"),
            positionSection.getDouble("Y"),
            positionSection.getDouble("z")
        );
        final Location location = origin.clone().add(0.5, 0.5, 0.5).add(position);
        final Effect effect = Effect.valueOf(this.section.getString("Id"));
        final short repetition = (short) section.getInt("Repeticao");
        return new EffectAnimation(scheduler, location, effect, repetition);
    }

    public static final class EffectAnimation implements Animation {
        private final @NotNull Scheduler scheduler;
        private final @NotNull Location location;
        private final @NotNull Effect effect;
        private final long repetition;
        private int taskId = -1;

        public EffectAnimation(@NotNull Scheduler scheduler, @NotNull Location location, @NotNull Effect effect, long repetition) {
            this.scheduler = scheduler;
            this.location = location;
            this.effect = effect;
            this.repetition = repetition;
        }

        public void run() {
            taskId = scheduler.runTimer(0L, repetition, false, this::animation).getTaskId();
        }

        private void animation() {
            this.location.getWorld().playEffect(this.location, this.effect, 0);
        }

        public void stop() {
            Bukkit.getScheduler().cancelTask(taskId);
        }
    }
}
