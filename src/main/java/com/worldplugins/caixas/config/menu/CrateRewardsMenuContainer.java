package com.worldplugins.caixas.config.menu;

import com.worldplugins.lib.common.Logger;
import com.worldplugins.lib.config.bukkit.ConfigContainer;
import com.worldplugins.lib.config.cache.annotation.MenuContainerOf;
import com.worldplugins.lib.config.cache.menu.MenuData;
import com.worldplugins.lib.config.cache.menu.StateMenuContainer;
import com.worldplugins.lib.extension.bukkit.ConfigurationExtensions;
import com.worldplugins.lib.extension.bukkit.MenuItemsExtension;
import com.worldplugins.lib.util.MenuDataUtils;
import lombok.NonNull;
import lombok.experimental.ExtensionMethod;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;

@ExtensionMethod({
    MenuItemsExtension.class,
    ConfigurationExtensions.class
})

@MenuContainerOf(name = "recompensas")
public class CrateRewardsMenuContainer extends StateMenuContainer {
    public CrateRewardsMenuContainer(Logger logger, @NonNull ConfigContainer configContainer, String section) {
        super(logger, configContainer, section);
    }

    @Override
    public MenuData createData(@NonNull ConfigurationSection section) {
        return MenuDataUtils.fetch(section)
            .modifyItems(items -> items.colorAll())
            .modifyData(dataSection -> new HashMap<String, Object>() {{
                put("Recompensa-iten", dataSection.getItem("Recompensa-iten"));
            }})
            .build();
    }
}
