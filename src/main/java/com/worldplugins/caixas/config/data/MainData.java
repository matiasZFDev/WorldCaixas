package com.worldplugins.caixas.config.data;

import com.worldplugins.caixas.config.data.animation.AnimationFactory;
import com.worldplugins.caixas.config.data.representation.CrateRepresentation;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class MainData {
    @RequiredArgsConstructor
    @Getter
    public static class Crate {
        private final @NonNull String id;
        private final @NonNull CrateRepresentation representation;
        private final @NonNull ItemStack keyItem;
        private final @NonNull AnimationFactory animationFactory;
    }

    @RequiredArgsConstructor
    public static class Crates {
        private final @NonNull List<Crate> crates;

        public Crate getById(@NonNull String id) {
            return crates.stream().filter(it -> it.getId().equals(id)).findFirst().orElse(null);
        }

        public @NonNull Collection<Crate> getAll() {
            return new ArrayList<>(crates);
        }
    }

    private final @NonNull ItemStack locatorItem;
    private final @NonNull List<Integer> rewardsSlots;
    private final @NonNull Crates crates;
}
