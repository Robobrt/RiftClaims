package com.riftproject.riftclaims.storage;

import com.riftproject.riftclaims.RiftClaims;
import com.riftproject.riftclaims.claim.Claim;
import com.riftproject.riftclaims.claim.Region;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StorageManager {

    private final RiftClaims plugin;
    private FileConfiguration claimsConfig;
    private final File claimsFile;

    public StorageManager(RiftClaims plugin) {
        this.plugin = plugin;
        this.claimsFile = new File(plugin.getDataFolder(), "claims.yml");
    }

    public List<Claim> loadClaims() {
        if (!claimsFile.exists()) {
            return new ArrayList<>();
        }
        claimsConfig = YamlConfiguration.loadConfiguration(claimsFile);
        List<Claim> claims = new ArrayList<>();
        ConfigurationSection claimsSection = claimsConfig.getConfigurationSection("claims");
        if (claimsSection == null) {
            return claims;
        }

        for (String ownerUUIDString : claimsSection.getKeys(false)) {
            try {
                UUID owner = UUID.fromString(ownerUUIDString);
                String worldName = claimsSection.getString(ownerUUIDString + ".world");
                World world = Bukkit.getWorld(worldName);
                if (world == null) {
                    plugin.getLogger().warning("World '" + worldName + "' not found for claim of " + ownerUUIDString + ". Skipping.");
                    continue;
                }
                int minX = claimsSection.getInt(ownerUUIDString + ".minX");
                int minY = claimsSection.getInt(ownerUUIDString + ".minY");
                int minZ = claimsSection.getInt(ownerUUIDString + ".minZ");
                int maxX = claimsSection.getInt(ownerUUIDString + ".maxX");
                int maxY = claimsSection.getInt(ownerUUIDString + ".maxY");
                int maxZ = claimsSection.getInt(ownerUUIDString + ".maxZ");

                Location pos1 = new Location(world, minX, minY, minZ);
                Location pos2 = new Location(world, maxX, maxY, maxZ);
                Region region = new Region(pos1, pos2);
                claims.add(new Claim(owner, region));
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid UUID found in claims.yml: " + ownerUUIDString);
            }
        }
        return claims;
    }

    public void saveClaims(List<Claim> claims) {
        claimsConfig = new YamlConfiguration();
        // Устанавливаем секцию, чтобы даже пустой файл имел структуру
        claimsConfig.createSection("claims"); 
        
        for (Claim claim : claims) {
            String ownerUUIDString = claim.getOwner().toString();
            ConfigurationSection claimSection = claimsConfig.createSection("claims." + ownerUUIDString);
            Region region = claim.getRegion();
            claimSection.set("world", region.getWorld().getName());
            claimSection.set("minX", region.getMinX());
            claimSection.set("minY", region.getMinY());
            claimSection.set("minZ", region.getMinZ());
            claimSection.set("maxX", region.getMaxX());
            claimSection.set("maxY", region.getMaxY());
            claimSection.set("maxZ", region.getMaxZ());
        }
        try {
            claimsConfig.save(claimsFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save claims to claims.yml!");
            e.printStackTrace();
        }
    }
}