package com.worldplugins.caixas.manager;

import com.worldplugins.caixas.config.data.LocationsData;
import com.worldplugins.caixas.config.data.MainData;
import com.worldplugins.caixas.config.data.animation.Animation;
import com.worldplugins.caixas.config.data.representation.CrateRepresentation;
import me.post.lib.common.Updatable;
import me.post.lib.config.model.ConfigModel;
import me.post.lib.util.Configurations;
import me.post.lib.util.Scheduler;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CrateManager implements Updatable {
    public static class LocatedCrate {
        private final @NotNull String id;
        private final @NotNull Location location;
        private final @NotNull CrateRepresentation.Handler representationHandler;
        private final @NotNull Animation animation;

        public LocatedCrate(
            @NotNull String id,
            @NotNull Location location,
            @NotNull CrateRepresentation.Handler representationHandler,
            @NotNull Animation animation
        ) {
            this.id = id;
            this.location = location;
            this.representationHandler = representationHandler;
            this.animation = animation;
        }

        public @NotNull String id() {
            return id;
        }

        public @NotNull Location location() {
            return location;
        }

        private void disable() {
            representationHandler.remove();
            animation.stop();
        }
    }

    private final @NotNull Scheduler scheduler;
    private final @NotNull ConfigModel<MainData> mainConfig;
    private final @NotNull ConfigModel<LocationsData> locationsDataConfig;
    private final @NotNull Collection<LocatedCrate> locatedCrates;

    public CrateManager(
        @NotNull Scheduler scheduler,
        @NotNull ConfigModel<MainData> mainConfig,
        @NotNull ConfigModel<LocationsData> locationsDataConfig
    ) {
        this.scheduler = scheduler;
        this.mainConfig = mainConfig;
        this.locationsDataConfig = locationsDataConfig;
        this.locatedCrates = new ArrayList<>();
    }

    private LocatedCrate getLocatedCrate(@NotNull String id) {
        return locatedCrates.stream()
            .filter(crate -> crate.id().equals(id))
            .findFirst()
            .orElse(null);
    }

    public void locateCrate(@NotNull Location location, @NotNull MainData.Crate crate) {
        final LocatedCrate locatedCrate = getLocatedCrate(crate.id());

        if (locatedCrate != null) {
            unlocateCrate(locatedCrate);
        }

        final CrateRepresentation.Handler representationHandler = crate.representation().spawn(location);
        final Animation animation = crate.animationFactory().create(scheduler, location);
        animation.run();
        locatedCrates.add(new LocatedCrate(crate.id(), location, representationHandler, animation));
        Configurations.update(locationsDataConfig, config ->
            config.set("Data." + crate.id(), location)
        );
    }

    public LocatedCrate getLocatedCrate(@NotNull Location location) {
        return locatedCrates.stream()
            .filter(crate -> crate.location().equals(location))
            .findFirst()
            .orElse(null);
    }

    public void unlocateCrate(@NotNull LocatedCrate locatedCrate) {
        locatedCrate.disable();
        locatedCrates.removeIf(current -> current.id().equals(locatedCrate.id));
        Configurations.update(locationsDataConfig, config ->
            config.set("Data." + locatedCrate.id(), null)
        );
    }

    @Override
    public void update() {
        disableAll();
        locatedCrates.clear();
        mainConfig.update();
        locationsDataConfig.update();

        locationsDataConfig.data().locations().forEach(current -> {
            final MainData.Crate crate = mainConfig.data().crates().getById(current.crateId());

            if (crate == null) {
                Configurations.update(locationsDataConfig, config ->
                    config.set("Data." + current.crateId(), null)
                );
                return;
            }

            locateCrate(current.location(), crate);
        });
    }

    public void disableAll() {
        locatedCrates.forEach(LocatedCrate::disable);
    }
}
