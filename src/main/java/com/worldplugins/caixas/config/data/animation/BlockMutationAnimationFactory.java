package com.worldplugins.caixas.config.data.animation;

import com.worldplugins.lib.config.common.BlockData;
import com.worldplugins.lib.util.ConfigSections;
import me.post.lib.util.Scheduler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

public class BlockMutationAnimationFactory implements AnimationFactory {
    private final @NotNull ConfigurationSection section;

    public BlockMutationAnimationFactory(@NotNull ConfigurationSection section) {
        this.section = section;
    }

    @Override
    public @NotNull Animation create(@NotNull Scheduler scheduler, @NotNull Location origin) {
        return new BlockMutationAnimation(
            scheduler,
            origin,
            section.getLong("Duracao"),
            section.getLong("Delay"),
            section.getLong("Pausa"),
            ConfigSections.map(
                section.getConfigurationSection("Blocos"),
                section -> new BlockData(ConfigSections.getItem(section))
            )
        );
    }

    public static class BlockMutationAnimation implements Animation {
        private final @NotNull Scheduler scheduler;
        private final @NotNull Location location;
        private final @NotNull BlockData originBlock;
        private final long duration;
        private long timeLeft;
        private final long delay;
        private long nextMutation;
        private final long pause;
        private long currentPause;
        private final LinkedList<BlockData> blocks;
        private int taskId = -1;

        @SuppressWarnings("deprecation")
        public BlockMutationAnimation(
            @NotNull Scheduler scheduler,
            @NotNull Location location,
            long duration,
            long delay,
            long pause,
            @NotNull List<BlockData> blocks
        ) {
            this.scheduler = scheduler;
            this.location = location;
            this.originBlock = new BlockData(new ItemStack(
                location.getBlock().getType(), location.getBlock().getData()
            ));
            this.duration = duration;
            this.timeLeft = duration;
            this.delay = delay;
            this.nextMutation = 0;
            this.pause = pause;
            this.currentPause = -1;
            this.blocks = new LinkedList<>(blocks);
        }

        @Override
        public void run() {
            taskId = scheduler.runTimer(1L, 1L, false, this::animation).getTaskId();
        }

        @SuppressWarnings("deprecation")
        private void animation() {
            if (currentPause > 0) {
                currentPause--;
                return;
            }

            if (currentPause == 0) {
                currentPause = -1;
                timeLeft = duration;
            }

            if (timeLeft > 0) {
                timeLeft--;

                if (nextMutation > 0) {
                    nextMutation--;
                    return;
                }

                int index = (int) Math.floor(Math.random() * blocks.size());
                BlockData block = blocks.get(index);

                if (
                    block.item().getType() == location.getBlock().getType() &&
                    block.item().getDurability() == location.getBlock().getData()
                ) {
                    index++;

                    if (index == blocks.size()) {
                        index = 0;
                    }

                    block = blocks.get(index);
                }

                block.set(location);
                nextMutation = delay;
                return;
            }

            originBlock.set(location);
            timeLeft = -1;
            currentPause = pause;
        }

        @Override
        public void stop() {
            Bukkit.getScheduler().cancelTask(taskId);
        }
    }
}
