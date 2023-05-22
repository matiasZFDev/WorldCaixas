package com.worldplugins.caixas.config.data.animation;

import java.util.*;

import com.worldplugins.caixas.NBTKeys;
import com.worldplugins.lib.util.ConfigSections;
import me.post.lib.util.NBTs;
import me.post.lib.util.Scheduler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class DropAnimationFactory implements AnimationFactory {
    private final @NotNull ConfigurationSection section;

    public DropAnimationFactory(@NotNull ConfigurationSection section) {
        this.section = section;
    }

    @Override
    public @NotNull Animation create(@NotNull Scheduler scheduler, @NotNull Location origin) {
        final Location location = origin.clone().add(new Vector(0.5, 1.0, 0.5));
        return new DropAnimation(
            scheduler,
            location,
            ConfigSections.map(
                section.getConfigurationSection("Itens"),
                current -> {
                    final ItemStack item = ConfigSections.getItem(current);
                    return NBTs.modifyTags(item, nbtItem -> nbtItem.setBoolean(NBTKeys.FAKE_DROP, true));
                }
            ),
            section.getLong("Vida-iten"),
            section.getLong("Delay"),
            (short) section.getInt("Repeticoes"),
            section.getLong("Pausa")
        );
    }

    public static final class TemporaryItem {
        private long timeLeft;
        private final @NotNull Item item;

        public TemporaryItem(long timeLeft, @NotNull Item item) {
            this.timeLeft = timeLeft;
            this.item = item;
        }

        public long timeLeft() {
            return timeLeft;
        }

        public void setTimeLeft(long timeLeft) {
            this.timeLeft = timeLeft;
        }

        public @NotNull Item item() {
            return item;
        }
    }

    public static final class DropAnimation implements Animation {
        private final @NotNull Scheduler scheduler;
        private final @NotNull Location origin;
        private final @NotNull List<ItemStack> items;
        private final long itemLifeTime;
        private final long dropDelay;
        private final short repetitions;
        private final long pause;
        private int taskId = -1;
        private final @NotNull List<TemporaryItem> droppedItems;
        private long nextDrop;
        private short nextPause;
        private long resume;

        public DropAnimation(
            @NotNull Scheduler scheduler,
            @NotNull Location origin,
            @NotNull List<ItemStack> items,
            long itemLifeTime,
            long dropDelay,
            short repetitions,
            long pause
        ) {
            this.scheduler = scheduler;
            this.origin = origin;
            this.items = items;
            this.itemLifeTime = itemLifeTime;
            this.dropDelay = dropDelay;
            this.repetitions = repetitions;
            this.pause = pause;

            final int maxLivingDroppedItems = (int) (pause / dropDelay);
            this.droppedItems = new ArrayList<>(maxLivingDroppedItems);
        }


        private @NotNull ItemStack rollItem() {
            return this.items.get((int) Math.floor((Math.random() * this.items.size())));
        }

        public void run() {
            taskId = scheduler.runTimer(1L, 1L, false, this::animation).getTaskId();
        }

        private void animation() {
            if (this.resume > 0L) {
                this.resume -= 1L;
            } else {
                this.droppedItems.removeIf(it -> {
                    if (it.timeLeft() > 0L) {
                        it.setTimeLeft(it.timeLeft() - 1);
                        return false;
                    } else {
                        it.item().remove();
                        return true;
                    }
                });

                if (this.nextDrop == 0L) {
                    final ItemStack nextItem = this.rollItem();
                    final Item item = this.origin.getWorld().dropItem(this.origin, nextItem);
                    this.nextDrop = this.dropDelay;
                    this.nextPause += 1;
                    this.droppedItems.add(new TemporaryItem(this.itemLifeTime, item));
                }

                this.nextDrop -= 1L;

                if (this.nextPause == 0) {
                    this.resume = this.pause;
                    this.nextPause = this.repetitions;
                }
            }
        }

        public void stop() {
            this.droppedItems.forEach(item -> item.item().remove());
            this.droppedItems.clear();
            Bukkit.getScheduler().cancelTask(this.taskId);
        }
    }
}
