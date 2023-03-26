package com.worldplugins.caixas.config.data.representation;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_8_R3.TileEntitySkull;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.Skull;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftSkull;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

@RequiredArgsConstructor
public class SkullBlock implements MeasuredCrateRepresentation {
    private final @NonNull String texture;

    @Override
    public @NonNull CrateRepresentation.Handler spawn(@NonNull Plugin plugin, @NonNull Location location) {
        location.getBlock().setType(Material.SKULL);
        final Skull skull = (Skull) location.getBlock().getState();
        skull.setSkullType(SkullType.PLAYER);
        final TileEntitySkull skullTile = ((CraftSkull) skull).getTileEntity();
        final GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", texture));
        skullTile.setGameProfile(profile);
        skull.update(true);
        return () -> {
            location.getBlock().setType(Material.AIR);
        };
    }

    @Override
    public float size() {
        return 0.5f;
    }
}
