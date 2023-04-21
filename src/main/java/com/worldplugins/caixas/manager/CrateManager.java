package com.worldplugins.caixas.manager;

import com.worldplugins.caixas.config.data.LocationsData;
import com.worldplugins.caixas.config.data.MainData;
import com.worldplugins.caixas.config.data.animation.Animation;
import com.worldplugins.caixas.config.data.representation.CrateRepresentation;
import com.worldplugins.lib.common.Updatable;
import com.worldplugins.lib.config.cache.ConfigCache;
import com.worldplugins.lib.extension.GenericExtensions;
import com.worldplugins.lib.util.ConfigUtils;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import java.util.*;

@ExtensionMethod({
    GenericExtensions.class
})

@RequiredArgsConstructor
public class CrateManager implements Updatable {


    @RequiredArgsConstructor
    public static class LocatedCrate {
        @Getter
        private final @NonNull String id;
        @Getter
        private final @NonNull Location location;
        private final @NonNull CrateRepresentation.Handler representationHandler;
        private final @NonNull Animation animation;

        private void disable() {
            representationHandler.remove();
            animation.stop();
        }
    }

    private final @NonNull Plugin plugin;
    private final @NonNull ConfigCache<MainData> mainConfig;
    private final @NonNull ConfigCache<LocationsData> locationsDataConfig;
    private final @NonNull List<LocatedCrate> locatedCrates = new ArrayList<>();

    private LocatedCrate getLocatedCrate(@NonNull String id) {
        return locatedCrates.stream()
            .filter(crate -> crate.getId().equals(id))
            .findFirst()
            .orElse(null);
    }

    public void locateCrate(@NonNull Location location, @NonNull MainData.Crate crate) {
        getLocatedCrate(crate.getId()).ifNotNull(this::unlocateCrate);
        final CrateRepresentation.Handler representationHandler = crate.getRepresentation().spawn(plugin, location);
        final Animation animation = crate.getAnimationFactory().create(plugin, location);
        animation.run();
        locatedCrates.add(new LocatedCrate(crate.getId(), location, representationHandler, animation));
        ConfigUtils.update(locationsDataConfig, config -> {
            config.set("Data." + crate.getId(), location);
        });
    }

    public LocatedCrate getLocatedCrate(@NonNull Location location) {
        return locatedCrates.stream()
            .filter(crate -> crate.getLocation().equals(location))
            .findFirst()
            .orElse(null);
    }

    public void unlocateCrate(@NonNull LocatedCrate locatedCrate) {
        locatedCrate.disable();
        locatedCrates.removeIf(current -> current.getId().equals(locatedCrate.id));
        ConfigUtils.update(locationsDataConfig, config -> {
            config.set("Data." + locatedCrate.getId(), null);
        });
    }

    @Override
    public void update() {
        disableAll();
        locatedCrates.clear();
        mainConfig.update();
        locationsDataConfig.update();

        locationsDataConfig.data().getLocations().forEach(current -> {
            final MainData.Crate crate = mainConfig.data().getCrates().getById(current.getCrateId());

            if (crate == null) {
                ConfigUtils.update(locationsDataConfig, config -> {
                    config.set("Data." + current.getCrateId(), null);
                });
                return;
            }

            locateCrate(current.getLocation(), crate);
        });
    }

    public void disableAll() {
        locatedCrates.forEach(LocatedCrate::disable);
    }
}
