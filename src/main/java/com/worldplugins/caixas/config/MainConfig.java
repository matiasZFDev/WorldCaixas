package com.worldplugins.caixas.config;

import com.worldplugins.caixas.NBTKeys;
import com.worldplugins.caixas.config.data.AnimationType;
import com.worldplugins.caixas.config.data.animation.AnimationFactory;
import com.worldplugins.caixas.config.data.animation.AnimationCompoundFactory;
import com.worldplugins.caixas.config.data.representation.*;
import com.worldplugins.lib.common.Logger;
import com.worldplugins.lib.config.bukkit.ConfigContainer;
import com.worldplugins.lib.config.cache.StateConfig;
import com.worldplugins.lib.config.cache.annotation.Config;
import com.worldplugins.lib.extension.bukkit.ColorExtensions;
import com.worldplugins.lib.extension.bukkit.ConfigurationExtensions;
import com.worldplugins.lib.extension.bukkit.ItemExtensions;
import com.worldplugins.lib.extension.bukkit.NBTExtensions;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@ExtensionMethod({
    ConfigurationExtensions.class,
    ColorExtensions.class,
    ItemExtensions.class,
    NBTExtensions.class
})

@Config(path = "config")
public class MainConfig extends StateConfig<MainConfig.Config> {
    @RequiredArgsConstructor
    @Getter
    public static class Config {
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

            public @NonNull Optional<Crate> getById(@NonNull String id) {
                return crates.stream().filter(it -> it.getId().equals(id)).findFirst();
            }

            public @NonNull Collection<Crate> getAll() {
                return new ArrayList<>(crates);
            }
        }

        private final @NonNull ItemStack locatorItem;
        private final @NonNull List<Integer> rewardsSlots;
        private final @NonNull Crates crates;
    }

    public MainConfig(Logger logger, @NonNull ConfigContainer configContainer) {
        super(logger, configContainer);
    }

    @Override
    public @NonNull Config fetch(@NonNull FileConfiguration config) {
        return new Config(
            config.getItem("Localizador-iten"),
            config.getIntegerList("Recompensas-slots"),
            new Config.Crates(fetchCrates(config.getConfigurationSection("Caixas")))
        );
    }

    private @NonNull List<Config.Crate> fetchCrates(@NonNull ConfigurationSection section) {

        return section.map(it -> {
            final String id = it.getString("Id");
            final @NonNull MeasuredCrateRepresentation baseRepresentation = fetchCrateRepresentation(
                it.getConfigurationSection("Representacao")
            );
            final List<String> hologramLines = it.getStringList("Holograma").color();
            return new Config.Crate(
                id,
                new HologrammedRepresentation(hologramLines, baseRepresentation),
                it.getItem("Chave-iten")
                    .colorMeta()
                    .addReference(NBTKeys.CRATE_KEY, id),
                fetchAnimations(it.getConfigurationSection("Animacoes"))
            );
        });
    }

    @SuppressWarnings("deprecation")
    private @NonNull MeasuredCrateRepresentation fetchCrateRepresentation(@NonNull ConfigurationSection section) {
        if (section.contains("Id"))
            return new NormalBlock(Material.getMaterial(section.getInt("Id")), (byte) section.getInt("Data"));

        if (section.contains("Skull-url"))
            return new SkullBlock(section.getString("Skull-url"));

        throw new Error("Uma das representações da caixa não é válida!");
    }

    private @NonNull AnimationFactory fetchAnimations(@NonNull ConfigurationSection section) {
        return new AnimationCompoundFactory(section.map(current -> {
            final String type = current.getString("Tipo");
            return AnimationType
                .find(type).orElseThrow(() -> new Error("Não existe nenhuma animação do tipo '" + type + "'."))
                .getFactory(current);
        }));
    }
}