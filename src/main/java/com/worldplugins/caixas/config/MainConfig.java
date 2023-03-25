package com.worldplugins.caixas.config;

import com.worldplugins.caixas.config.data.animation.AnimationFactory;
import com.worldplugins.caixas.config.data.animation.DropAnimationFactory;
import com.worldplugins.caixas.config.data.animation.EffectAnimationFactory;
import com.worldplugins.caixas.config.data.animation.AnimationCompoundFactory;
import com.worldplugins.caixas.config.data.representation.CrateRepresentation;
import com.worldplugins.caixas.config.data.representation.NormalBlock;
import com.worldplugins.caixas.config.data.representation.SkullBlock;
import com.worldplugins.lib.common.Logger;
import com.worldplugins.lib.config.bukkit.ConfigContainer;
import com.worldplugins.lib.config.cache.StateConfig;
import com.worldplugins.lib.extension.bukkit.ConfigurationExtensions;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@ExtensionMethod({
    ConfigurationExtensions.class
})

public class MainConfig extends StateConfig<MainConfig.Config> {
    @RequiredArgsConstructor
    @Getter
    public static class Config {
        @RequiredArgsConstructor
        @Getter
        public static class Crate {
            private final @NonNull String id;
            private final @NonNull List<String> hologramLines;
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
        private final @NonNull Crates crates;
    }

    private final @NonNull Plugin plugin;

    public MainConfig(Logger logger, @NonNull ConfigContainer configContainer, @NonNull Plugin plugin) {
        super(logger, configContainer);
        this.plugin = plugin;
    }

    @Override
    public @NonNull Config fetch(@NonNull FileConfiguration config) {
        return new Config(
            config.getItem("Localizador-iten"),
            new Config.Crates(fetchCrates(config.getConfigurationSection("Caixas")))
        );
    }

    private @NonNull List<Config.Crate> fetchCrates(@NonNull ConfigurationSection section) {
        return section.map(it -> new Config.Crate(
            it.getString("Id"),
            it.getStringList("Holograma"),
            fetchCrateRepresentation(it.getConfigurationSection("Representacao")),
            it.getItem("Chave-iten"),
            fetchAnimations(it.getConfigurationSection("Animacoes"))
        ));
    }

    @SuppressWarnings("deprecation")
    private @NonNull CrateRepresentation fetchCrateRepresentation(@NonNull ConfigurationSection section) {
        if (section.contains("Id"))
            return new NormalBlock(Material.getMaterial(section.getInt("Id")), (byte) section.getInt("Data"));

        if (section.contains("Skull-url"))
            return new SkullBlock(section.getString("Skull-url"));

        throw new Error("Uma das representações da caixa não é válida!");
    }

    private @NonNull AnimationFactory fetchAnimations(@NonNull ConfigurationSection section) {
        return new AnimationCompoundFactory(section.map(current -> {
            final String type = current.getString("Tipo");

            switch (type) {
                case "DROP":
                    return new DropAnimationFactory(plugin, section);
                case "EFEITO":
                    return new EffectAnimationFactory(plugin, section);
                default:
                    throw new Error("Não existe nenhuma animação do tipo '" + type + "'.");
            }
        }));
    }
}