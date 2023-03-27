package com.worldplugins.caixas.config.data.animation;

import com.worldplugins.lib.config.data.BlockData;
import com.worldplugins.lib.extension.bukkit.ConfigurationExtensions;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.LinkedList;
import java.util.List;

@ExtensionMethod({
    ConfigurationExtensions.class
})

@RequiredArgsConstructor
public class BlockMutationAnimationFactory implements AnimationFactory {
    private final @NonNull ConfigurationSection section;

    @Override
    public @NonNull Animation create(@NonNull Plugin plugin, @NonNull Location origin) {
        return new BlockMutationAnimation(
            plugin,
            origin,
            section.getLong("Duracao"),
            section.getLong("Delay"),
            section.getLong("Pausa"),
            section.getConfigurationSection("Blocos").map(section -> section.blockData())
        );
    }

    public static class BlockMutationAnimation implements Animation {
        private final @NonNull Plugin plugin;
        private final @NonNull Location location;
        private final @NonNull BlockData originBlock;
        private final long duration;
        private long timeLeft;
        private final long delay;
        private long nextMutation;
        private final long pause;
        private long currentPause;
        private final LinkedList<BlockData> blocks;
        private int taskId = -1;

        public BlockMutationAnimation(
            @NonNull Plugin plugin,
            @NonNull Location location,
            long duration,
            long delay,
            long pause,
            @NonNull List<BlockData> blocks
        ) {
            this.plugin = plugin;
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
            taskId = Bukkit.getScheduler().runTaskTimer(plugin, this::animation, 1L, 1L).getTaskId();
        }

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
                    block.getItem().getType() == location.getBlock().getType() &&
                    block.getItem().getDurability() == location.getBlock().getData()
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
