package com.worldplugins.caixas.config.data;

import com.worldplugins.caixas.config.data.animation.AnimationFactory;
import com.worldplugins.caixas.config.data.representation.CrateRepresentation;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MainData {
    public static class Crate {
        private final @NotNull String id;
        private final @NotNull CrateRepresentation representation;
        private final @NotNull ItemStack keyItem;
        private final @NotNull AnimationFactory animationFactory;

        public Crate(
            @NotNull String id,
            @NotNull CrateRepresentation representation,
            @NotNull ItemStack keyItem,
            @NotNull AnimationFactory animationFactory
        ) {
            this.id = id;
            this.representation = representation;
            this.keyItem = keyItem;
            this.animationFactory = animationFactory;
        }

        public @NotNull String id() {
            return id;
        }

        public @NotNull CrateRepresentation representation() {
            return representation;
        }

        public @NotNull ItemStack getKeyItem() {
            return keyItem;
        }

        public @NotNull AnimationFactory animationFactory() {
            return animationFactory;
        }
    }

    public static class Crates {
        private final @NotNull List<Crate> crates;

        public Crates(@NotNull List<Crate> crates) {
            this.crates = crates;
        }

        public @NotNull List<Crate> crates() {
            return crates;
        }

        public Crate getById(@NotNull String id) {
            return crates.stream().filter(it -> it.id().equals(id)).findFirst().orElse(null);
        }

        public @NotNull Collection<Crate> getAll() {
            return new ArrayList<>(crates);
        }
    }

    private final @NotNull ItemStack locatorItem;
    private final @NotNull Crates crates;

    public MainData(@NotNull ItemStack locatorItem, @NotNull Crates crates) {
        this.locatorItem = locatorItem;
        this.crates = crates;
    }

    public @NotNull ItemStack locatorItem() {
        return locatorItem;
    }

    public @NotNull Crates crates() {
        return crates;
    }
}
