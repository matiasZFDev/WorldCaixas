package com.worldplugins.caixas.config.data.animation;

import java.util.*;

import com.worldplugins.lib.extension.bukkit.ConfigurationExtensions;
import com.worldplugins.lib.util.ItemUtils;
import lombok.*;
import lombok.experimental.ExtensionMethod;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

@ExtensionMethod({
    ConfigurationExtensions.class
})

@RequiredArgsConstructor
public class DropAnimationFactory implements AnimationFactory {
    private final @NonNull Plugin plugin;
    private final @NonNull ConfigurationSection section;

    public @NonNull Animation create(@NonNull Location origin) {
        final Location location = origin.clone().add(new Vector(0.5, 1.0, 0.5));
        return new DropAnimation(
            plugin,
            location,
            section.getConfigurationSection("Itens").map(ItemUtils::buildFromSectionNoMeta),
            section.getLong("Vida-iten"),
            section.getLong("Delay"),
            (short) section.getInt("Repeticoes"),
            section.getLong("Pausa")
        );
    }

    @AllArgsConstructor
    public static final class TemporaryItem {
        @Setter
        @Getter
        private long timeLeft;
        @Getter
        private final @NonNull Item item;
    }

    public static final class DropAnimation implements Animation {
        private final @NonNull Plugin plugin;
        private final @NonNull Location origin;
        private final @NonNull List<ItemStack> items;
        private final long itemLifeTime;
        private final long dropDelay;
        private final short repetitions;
        private final long pause;
        private int taskId = -1;
        private final @NonNull List<TemporaryItem> droppedItems;
        private long nextDrop;
        private short nextPause;
        private long resume;

        public DropAnimation(@NonNull Plugin plugin, @NonNull Location origin, @NonNull List<ItemStack> items, long itemLifeTime, long dropDelay, short repetitions, long pause) {
            this.plugin = plugin;
            this.origin = origin;
            this.items = items;
            this.itemLifeTime = itemLifeTime;
            this.dropDelay = dropDelay;
            this.repetitions = repetitions;
            this.pause = pause;

            final int maxLivingDroppedItems = (int) (pause / dropDelay);
            this.droppedItems = new ArrayList<>(maxLivingDroppedItems);
        }


        private @NonNull ItemStack rollItem() {
            return this.items.get((int) Math.round((Math.random() * this.items.size())));
        }

        public void run() {
            this.taskId = Bukkit.getScheduler().runTaskTimer(this.plugin, this::animation, 1L, 1L).getTaskId();
        }

        private void animation() {
            if (this.resume > 0L) {
                this.resume -= 1L;
            } else {
                this.droppedItems.removeIf(it -> {
                    if (it.getTimeLeft() > 0L) {
                        it.setTimeLeft(it.getTimeLeft() - 1);
                        return false;
                    } else {
                        it.getItem().remove();
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
            this.droppedItems.forEach(item -> item.getItem().remove());
            this.droppedItems.clear();
            Bukkit.getScheduler().cancelTask(this.taskId);
        }
    }
}
