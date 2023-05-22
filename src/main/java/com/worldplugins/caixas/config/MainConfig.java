package com.worldplugins.caixas.config;

import com.worldplugins.caixas.NBTKeys;
import com.worldplugins.caixas.config.data.MainData;
import com.worldplugins.caixas.config.data.animation.AnimationType;
import com.worldplugins.caixas.config.data.animation.AnimationFactory;
import com.worldplugins.caixas.config.data.animation.AnimationCompoundFactory;
import com.worldplugins.caixas.config.data.representation.*;
import com.worldplugins.lib.util.ConfigSections;
import com.worldplugins.lib.util.Pipe;
import me.post.lib.config.model.ConfigModel;
import me.post.lib.config.wrapper.ConfigWrapper;
import me.post.lib.util.Colors;
import me.post.lib.util.Items;
import me.post.lib.util.NBTs;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.List;

public class MainConfig implements ConfigModel<MainData> {
    private @UnknownNullability MainData data;
    private final @NotNull ConfigWrapper configWrapper;

    public MainConfig(@NotNull ConfigWrapper configWrapper) {
        this.configWrapper = configWrapper;
    }

    @Override
    public void update() {
        final FileConfiguration config = configWrapper.unwrap();
        data = new MainData(
            Items.colorMeta(ConfigSections.getItem(config.getConfigurationSection("Localizador-iten"))),
            new MainData.Crates(fetchCrates(config.getConfigurationSection("Caixas")))
        );
    }

    @Override
    public @NotNull MainData data() {
        return data;
    }

    @Override
    public @NotNull ConfigWrapper wrapper() {
        return configWrapper;
    }

    private @NotNull List<MainData.Crate> fetchCrates(@NotNull ConfigurationSection section) {
        return ConfigSections.map(section, key -> {
            final String id = key.getString("Id");
            final @NotNull MeasuredCrateRepresentation baseRepresentation = fetchCrateRepresentation(
                key.getConfigurationSection("Representacao")
            );
            final List<String> hologramLines = Colors.color(key.getStringList("Holograma"));
            return new MainData.Crate(
                id,
                new HologrammedRepresentation(configWrapper.plugin(), hologramLines, baseRepresentation),
                Pipe.of(ConfigSections.getItem(key.getConfigurationSection("Chave-iten")))
                    .apply(Items::colorMeta)
                    .apply(item -> NBTs.modifyTags(item, nbtItem ->
                        nbtItem.setString(NBTKeys.CRATE_KEY, id)
                    ))
                    .obtain(),
                fetchAnimations(key.getConfigurationSection("Animacoes"))
            );
        });
    }

    @SuppressWarnings("deprecation")
    private @NotNull MeasuredCrateRepresentation fetchCrateRepresentation(@NotNull ConfigurationSection section) {
        if (section.contains("Id")) {
            return new NormalBlock(Material.getMaterial(section.getInt("Id")), (byte) section.getInt("Data"));
        }

        if (section.contains("Skull-url")) {
            return new SkullBlock(ConfigSections.getItem(section));
        }

        throw new Error("Uma das representações da caixa não é válida!");
    }

    private @NotNull AnimationFactory fetchAnimations(@NotNull ConfigurationSection section) {
        return new AnimationCompoundFactory(ConfigSections.map(section, current -> {
            final String type = current.getString("Tipo");
            final AnimationType animationType = AnimationType.find(type);

            if (animationType == null)
                throw new Error("Não existe nenhuma animação do tipo '" + type + "'.");

            return animationType.getFactory(current);
        }));
    }
}