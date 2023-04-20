package com.worldplugins.caixas.config;

import com.worldplugins.caixas.NBTKeys;
import com.worldplugins.caixas.config.data.MainData;
import com.worldplugins.caixas.config.data.animation.AnimationType;
import com.worldplugins.caixas.config.data.animation.AnimationFactory;
import com.worldplugins.caixas.config.data.animation.AnimationCompoundFactory;
import com.worldplugins.caixas.config.data.representation.*;
import com.worldplugins.lib.config.cache.InjectedConfigCache;
import com.worldplugins.lib.config.cache.annotation.ConfigSpec;
import com.worldplugins.lib.extension.bukkit.ColorExtensions;
import com.worldplugins.lib.extension.bukkit.ConfigurationExtensions;
import com.worldplugins.lib.extension.bukkit.ItemExtensions;
import com.worldplugins.lib.extension.bukkit.NBTExtensions;
import lombok.NonNull;
import lombok.experimental.ExtensionMethod;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

@ExtensionMethod({
    ConfigurationExtensions.class,
    ColorExtensions.class,
    ItemExtensions.class,
    NBTExtensions.class
})

public class MainConfig implements InjectedConfigCache<MainData> {
    @ConfigSpec(path = "config")
    public @NonNull MainData transform(@NonNull FileConfiguration config) {
        return new MainData(
            config.getItem("Localizador-iten"),
            config.getIntegerList("Recompensas-slots"),
            new MainData.Crates(fetchCrates(config.getConfigurationSection("Caixas")))
        );
    }

    private @NonNull List<MainData.Crate> fetchCrates(@NonNull ConfigurationSection section) {

        return section.map(it -> {
            final String id = it.getString("Id");
            final @NonNull MeasuredCrateRepresentation baseRepresentation = fetchCrateRepresentation(
                it.getConfigurationSection("Representacao")
            );
            final List<String> hologramLines = it.getStringList("Holograma").color();
            return new MainData.Crate(
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